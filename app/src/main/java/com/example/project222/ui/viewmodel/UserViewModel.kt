package com.example.project222.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project222.data.local.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _allChefs = MutableStateFlow<List<User>>(emptyList())
    val allChefs = _allChefs.asStateFlow()

    // Deriving favoriteChefs reactively from allChefs and currentUser
    val favoriteChefs: StateFlow<List<User>> = combine(allChefs, currentUser) { chefs, user ->
        val favoriteIds = user?.favoriteCookIds ?: emptyList()
        chefs.filter { favoriteIds.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    init {
        // Check if user is already signed in
        auth.currentUser?.let { firebaseUser ->
            fetchUserData(firebaseUser.uid)
        }
        fetchAllChefs()
    }

    private fun fetchUserData(uid: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("users").document(uid).get().await()
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val email = document.getString("email") ?: ""
                    val profilePicUrl = document.getString("profilePicUrl")
                    val specialty = document.getString("specialty")
                    val bio = document.getString("bio")
                    val pricePerHour = document.getDouble("pricePerHour")
                    val availability = document.getString("availability")
                    val favoriteCookIds = (document.get("favoriteCookIds") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
                    
                    _currentUser.value = User(
                        id = uid, 
                        name = name, 
                        email = email, 
                        profilePicUrl = profilePicUrl,
                        specialty = specialty,
                        bio = bio,
                        pricePerHour = pricePerHour,
                        availability = availability,
                        favoriteCookIds = favoriteCookIds
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchAllChefs() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .whereNotEqualTo("specialty", null)
                    .get().await()
                
                val chefs = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: ""
                    val email = doc.getString("email") ?: ""
                    val profilePicUrl = doc.getString("profilePicUrl")
                    val specialty = doc.getString("specialty")
                    val bio = doc.getString("bio")
                    val pricePerHour = doc.getDouble("pricePerHour")
                    val availability = doc.getString("availability")
                    
                    User(
                        id = doc.id,
                        name = name,
                        email = email,
                        profilePicUrl = profilePicUrl,
                        specialty = specialty,
                        bio = bio,
                        pricePerHour = pricePerHour,
                        availability = availability
                    )
                }
                _allChefs.value = chefs
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val userMap = hashMapOf(
                        "id" to firebaseUser.uid,
                        "name" to name,
                        "email" to email,
                        "profilePicUrl" to null,
                        "specialty" to null,
                        "bio" to null,
                        "pricePerHour" to null,
                        "availability" to null,
                        "favoriteCookIds" to emptyList<String>()
                    )
                    db.collection("users").document(firebaseUser.uid).set(userMap).await()
                    _currentUser.value = User(id = firebaseUser.uid, name = name, email = email)
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    fetchUserData(firebaseUser.uid)
                    onSuccess()
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _isUploading.value = true
            try {
                val ref = storage.reference.child("profile_pics/$uid.jpg")
                ref.putFile(uri).await()
                val downloadUrl = ref.downloadUrl.await().toString()
                val urlWithTimestamp = "$downloadUrl?t=${System.currentTimeMillis()}"
                
                db.collection("users").document(uid).update("profilePicUrl", urlWithTimestamp).await()
                _currentUser.value = _currentUser.value?.copy(profilePicUrl = urlWithTimestamp)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun updateChefProfile(
        name: String, 
        specialty: String, 
        price: Double, 
        bio: String, 
        availability: String,
        onSuccess: () -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val chefMap = hashMapOf(
                    "name" to name,
                    "specialty" to specialty,
                    "pricePerHour" to price,
                    "bio" to bio,
                    "availability" to availability
                )
                db.collection("users").document(uid).update(chefMap as Map<String, Any>).await()
                
                _currentUser.value = _currentUser.value?.copy(
                    name = name,
                    specialty = specialty,
                    pricePerHour = price,
                    bio = bio,
                    availability = availability
                )
                fetchAllChefs() // Refresh the list
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite(cookId: String) {
        val uid = auth.currentUser?.uid ?: return
        val currentFavorites = _currentUser.value?.favoriteCookIds ?: emptyList()
        val isFavorite = currentFavorites.contains(cookId)
        
        viewModelScope.launch {
            try {
                if (isFavorite) {
                    db.collection("users").document(uid).update("favoriteCookIds", FieldValue.arrayRemove(cookId)).await()
                    _currentUser.value = _currentUser.value?.copy(
                        favoriteCookIds = currentFavorites.filter { it != cookId }
                    )
                } else {
                    db.collection("users").document(uid).update("favoriteCookIds", FieldValue.arrayUnion(cookId)).await()
                    _currentUser.value = _currentUser.value?.copy(
                        favoriteCookIds = currentFavorites + cookId
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun searchUsers(query: String) {
        if (query.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get().await()
                
                val users = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: ""
                    val email = doc.getString("email") ?: ""
                    val profilePicUrl = doc.getString("profilePicUrl")
                    val specialty = doc.getString("specialty")
                    val bio = doc.getString("bio")
                    val pricePerHour = doc.getDouble("pricePerHour")
                    val availability = doc.getString("availability")

                    User(
                        id = doc.id,
                        name = name,
                        email = email,
                        profilePicUrl = profilePicUrl,
                        specialty = specialty,
                        bio = bio,
                        pricePerHour = pricePerHour,
                        availability = availability
                    )
                }
                _searchResults.value = users
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }
}
