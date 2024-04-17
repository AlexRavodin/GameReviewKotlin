package com.example.gamereview.providers

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthProvider {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createUser(email: String, password: String): Task<FirebaseUser?> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    return@continueWithTask Tasks.forResult(firebaseAuth.currentUser)
                } else {
                    throw task.exception ?: Exception("Unknown error occurred")
                }
            }
    }

    fun signIn(email: String, password: String): Task<FirebaseUser?> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    return@continueWithTask Tasks.forResult(firebaseAuth.currentUser)
                } else {
                    throw task.exception ?: Exception("Unknown error occurred")
                }
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun deleteUser(): Task<Void> {
        val user = firebaseAuth.currentUser ?: throw Exception("User is not valid.")

        return user.delete()
    }
}