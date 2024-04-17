package com.example.gamereview.providers

import com.example.gamereview.models.Game
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class GameProvider {

    private val firestore = FirebaseFirestore.getInstance()
    private var imageProvider = ImageProvider()

    companion object {
        const val GAME_COLLECTION = "games"
        const val FAVORITE_ITEM_COLLECTION = "favorite_items"
        const val IMAGE_COLLECTION = "game_images"
    }

    suspend fun fetchGameById(gameId: String): Game? {
        return try {
            val documentSnapshot = firestore.collection(GAME_COLLECTION).document(gameId).get().await()
            if (documentSnapshot.exists()) {
                Game.fromFirestore(documentSnapshot)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun fetchFavoriteGamesForUser(userId: String): Flow<List<Game>> = callbackFlow {
        val listener = firestore.collection(FAVORITE_ITEM_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val gameIds = snapshot?.documents?.mapNotNull { it.getString("gameId") } ?: emptyList()
                val games = mutableListOf<Game>()

                gameIds.forEach { gameId ->
                    firestore.collection(GAME_COLLECTION).document(gameId).get()
                        .addOnSuccessListener { document ->
                            Game.fromFirestore(document).let {
                                games.add(it)
                                trySend(games.toList())
                            }
                        }
                }
            }

        awaitClose { listener.remove() }
    }

    fun fetchAllGames(): Flow<List<Game>> = callbackFlow {
        val listener = firestore.collection(GAME_COLLECTION)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }

                val games = snapshot?.documents?.mapNotNull { Game.fromFirestore(it) } ?: emptyList()
                trySend(games)
            }

        awaitClose { listener.remove() }
    }

    suspend fun toggleFavoriteStatus(userId: String, gameId: String) {
        val collection = firestore.collection(FAVORITE_ITEM_COLLECTION)
        val querySnapshot = collection
            .whereEqualTo("userId", userId)
            .whereEqualTo("gameId", gameId)
            .get()
            .await()

        if (querySnapshot.isEmpty) {
            collection.add(mapOf(
                "userId" to userId,
                "gameId" to gameId
            )).await()
        } else {
            querySnapshot.documents.first().reference.delete().await()
        }
    }

    suspend fun isFavorite(userId: String, gameId: String): Boolean {
        val snapshot = firestore.collection(FAVORITE_ITEM_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("gameId", gameId)
            .get()
            .await()

        return snapshot.documents.isNotEmpty()
    }

    suspend fun fetchImageUrls(gameId: String): List<String> {
        val snapshot = firestore.collection(IMAGE_COLLECTION)
            .whereEqualTo("gameId", gameId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            imageProvider.normalizeFirebaseUrl(document.getString("url") ?: "")
        }
    }
}