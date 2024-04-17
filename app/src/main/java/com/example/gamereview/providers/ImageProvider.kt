package com.example.gamereview.providers

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ImageProvider {

    private val storage = FirebaseStorage.getInstance()

    suspend fun normalizeFirebaseUrl(imagePath: String): String {
        return storage.reference.child(imagePath).downloadUrl.await().toString()
    }
}