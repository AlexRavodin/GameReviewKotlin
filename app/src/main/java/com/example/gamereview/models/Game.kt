package com.example.gamereview.models

import com.google.firebase.firestore.DocumentSnapshot

data class Game(
    val id: String,
    val name: String,
    val description: String,
    val genre: String,
    val minimumAge: Int
) {
    companion object {
        fun fromFirestore(doc: DocumentSnapshot): Game {
            val data = doc.data as Map<String, Any>?
            return Game(
                doc.id,
                data?.get("name") as? String ?: "",
                data?.get("description") as? String ?: "",
                data?.get("genre") as? String ?: "",
                (data?.get("minimumAge") as? Long ?: 0L).toInt()
            )
        }
    }
}