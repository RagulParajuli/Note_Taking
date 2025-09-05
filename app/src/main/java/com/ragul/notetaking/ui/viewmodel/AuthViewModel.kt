package com.ragul.notetaking.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ragul.notetaking.data.database.NoteDatabase
import com.ragul.notetaking.data.model.User
import com.ragul.notetaking.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userRepository: UserRepository

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        val userDao = NoteDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)
        viewModelScope.launch(Dispatchers.IO) {
            val local = userRepository.getCurrentUser()
            _currentUser.value = local
        }
    }

    fun isSignedIn(): Boolean = _currentUser.value != null

    fun signInWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                viewModelScope.launch(Dispatchers.IO) {
                    firebaseUser?.let {
                        val user = User(uid = it.uid, email = it.email ?: email, displayName = it.displayName)
                        userRepository.upsert(user)
                        _currentUser.value = user
                    }
                    withContext(Dispatchers.Main) { onResult(true, null) }
                }
            } else {
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                viewModelScope.launch(Dispatchers.IO) {
                    firebaseUser?.let {
                        val user = User(uid = it.uid, email = it.email ?: email, displayName = it.displayName)
                        userRepository.upsert(user)
                        _currentUser.value = user
                    }
                    withContext(Dispatchers.Main) { onResult(true, null) }
                }
            } else {
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = auth.currentUser
                viewModelScope.launch(Dispatchers.IO) {
                    firebaseUser?.let {
                        val user = User(uid = it.uid, email = it.email ?: "", displayName = it.displayName)
                        userRepository.upsert(user)
                        _currentUser.value = user
                    }
                    withContext(Dispatchers.Main) { onResult(true, null) }
                }
            } else {
                onResult(false, task.exception?.localizedMessage)
            }
        }
    }

    fun fetchSignInMethodsForEmail(email: String, onResult: (List<String>?, String?) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(task.result?.signInMethods, null)
            } else {
                onResult(null, task.exception?.localizedMessage)
            }
        }
    }

    fun signOut(onResult: (Boolean) -> Unit = {}) {
        auth.signOut()
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.clearAll()
            _currentUser.value = null
            withContext(Dispatchers.Main) { onResult(true) }
        }
    }
}
