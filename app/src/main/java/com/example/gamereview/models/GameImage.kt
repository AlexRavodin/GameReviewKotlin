package com.example.gamereview.models

import com.google.firebase.firestore.DocumentSnapshot

data class GameImage(
    val id: String,
    val gameId: String,
    val url: String
) {
    companion object {
        fun fromFirestore(doc: DocumentSnapshot): GameImage {
            val data = doc.data as Map<String, Any>
            return GameImage(
                doc.id,
                data["gameId"] as? String ?: "",
                data["url"] as? String ?: ""
            )
        }
    }
}