package com.example.gamereview.providers

import com.example.gamereview.models.AppUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserProvider {
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val USER_COLLECTION = "users"
    }

    suspend fun updateUser(user: AppUser) {
        val snapshot = firestore.collection(USER_COLLECTION)
            .whereEqualTo("authId", user.authId)
            .get()
            .await()

        if (snapshot.documents.isNotEmpty()) {
            val document = snapshot.documents.first()
            document.reference.update(mapOf(
                "authId" to user.authId,
                "name" to user.name,
                "email" to user.email,
                "favoriteGenre" to user.favoriteGenre,
                "favoriteGame" to user.favoriteGame,
                "age" to user.age
            )).await()
        } else {
            throw Exception()
        }
    }

    suspend fun addUser(user: AppUser) {
        firestore.collection(USER_COLLECTION).add(mapOf(
            "authId" to user.authId,
            "name" to user.name,
            "email" to user.email,
            "favoriteGenre" to user.favoriteGenre,
            "favoriteGame" to user.favoriteGame,
            "age" to user.age
        )).await()
    }

    suspend fun loadUser(userId: String): AppUser? {
        return fetchUserData(userId)
    }

    suspend fun deleteUser(authId: String) {
        val snapshot = firestore.collection(USER_COLLECTION)
            .whereEqualTo("authId", authId)
            .get()
            .await()

        if (snapshot.documents.isNotEmpty()) {
            val document = snapshot.documents.first()
            document.reference.delete().await()
        } else {
            throw Exception()
        }
    }

    private suspend fun fetchUserData(authId: String): AppUser? {
        val userDoc = firestore.collection(USER_COLLECTION)
            .whereEqualTo("authId", authId)
            .limit(1)
            .get()
            .await()

        return if (userDoc.documents.isNotEmpty()) {
            AppUser.fromFirestore(userDoc.documents.first())
        } else {
            null
        }
    }
}