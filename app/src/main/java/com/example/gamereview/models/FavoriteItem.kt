package com.example.gamereview.models

import com.google.firebase.firestore.DocumentSnapshot

data class FavoriteItem(
    val id: String,
    val userId: String,
    val gameId: String
) {
    companion object {
        fun fromFirestore(doc: DocumentSnapshot): FavoriteItem {
            val data = doc.data as Map<String, Any>
            return FavoriteItem(
                doc.id,
                data["userId"] as? String ?: "",
                data["gameId"] as? String ?: ""
            )
        }
    }
}