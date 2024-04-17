package com.example.gamereview.activities

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.gamereview.R
import com.example.gamereview.adapters.ImageSliderAdapter
import com.example.gamereview.models.Game
import com.example.gamereview.providers.GameProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GameDetailsActivity : AppCompatActivity() {
    private lateinit var gameProvider: GameProvider
    private lateinit var game: Game
    private var isFavorite: Boolean = false

    private lateinit var favoriteSwitch: Switch
    private lateinit var descriptionTextView: TextView
    private lateinit var genresTextView: TextView
    private lateinit var minimumAgeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        descriptionTextView = findViewById(R.id.descriptionTextView)
        genresTextView = findViewById(R.id.genresTextView)
        minimumAgeTextView = findViewById(R.id.minimumAgeTextView)
        favoriteSwitch = findViewById(R.id.favoriteSwitch)

        val gameId = intent.getStringExtra("gameId")!!
        val userId = intent.getStringExtra("userId")!!

        gameProvider = GameProvider()

        CoroutineScope(Dispatchers.Main).launch {
            val gameData = gameProvider.fetchGameById(gameId)
            if (gameData == null) {
                Toast.makeText(this@GameDetailsActivity, "Game not found", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            game = gameData
            loadGameDetails()
            checkFavoriteStatus(userId)

            favoriteSwitch.setOnCheckedChangeListener { _, isChecked ->
                CoroutineScope(Dispatchers.Main).launch {
                    toggleFavoriteStatus(userId, isChecked)
                }
            }
        }
    }

    private fun setupViewPager(images: List<String>) {
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerImageSlider)
        viewPager.adapter = ImageSliderAdapter(this, images)
    }

    private suspend fun loadGameDetails() {
        title = game.name
        descriptionTextView.text = getString(R.string.description, game.description)
        genresTextView.text = getString(R.string.genres, game.genre)
        minimumAgeTextView.text = getString(R.string.minimum_age, game.minimumAge.toString())

        val imageUrls = gameProvider.fetchImageUrls(game.id)
        setupViewPager(imageUrls)
    }

    private suspend fun checkFavoriteStatus(userId: String) {
        try {
            val isFavorite = gameProvider.isFavorite(userId, game.id)
            this.isFavorite = isFavorite
            updateFavoriteSwitch()
        } catch (exception: Exception) {
            Toast.makeText(this, "Failed to check favorite status: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun toggleFavoriteStatus(userId: String, isChecked: Boolean) {
        try {
            gameProvider.toggleFavoriteStatus(userId, game.id)
            isFavorite = isChecked
            val message = if (isChecked) "Added to favorites" else "Removed from favorites"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } catch (exception: Exception) {
            val message = if (isChecked) "Failed to add to favorites" else "Failed to remove from favorites"
            Toast.makeText(this, "$message: ${exception.message}", Toast.LENGTH_SHORT).show()
            favoriteSwitch.isChecked = !isChecked
        }
    }

    private fun updateFavoriteSwitch() {
        favoriteSwitch.isChecked = isFavorite
    }
}