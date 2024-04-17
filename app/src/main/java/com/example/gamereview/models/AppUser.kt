package com.example.gamereview.models

import com.google.firebase.firestore.DocumentSnapshot

data class AppUser(
    val documentId: String,
    val authId: String,
    val email: String,
    val name: String,
    val favoriteGenre: String,
    val favoriteGame: String,
    val age: Int
) {
    companion object {
        fun fromFirestore(doc: DocumentSnapshot): AppUser {
            val data = doc.data as Map<String, Any>
            return AppUser(
                doc.id,
                data["authId"] as? String ?: "",
                data["email"] as? String ?: "",
                data["name"] as? String ?: "",
                data["favoriteGenre"] as? String ?: "",
                data["favoriteGame"] as? String ?: "",
                (data["age"] as? Long ?: 0L).toInt()
            )
        }
    }
}