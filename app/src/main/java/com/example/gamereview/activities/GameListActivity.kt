package com.example.gamereview.activities

import android.content.Intent
import com.example.gamereview.R
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gamereview.adapters.GameListAdapter
import com.example.gamereview.models.Game
import com.example.gamereview.providers.GameProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GameListActivity : AppCompatActivity() {

    private var userId : String? = null
    private var showFavoritesOnly = false
    private val gameProvider = GameProvider()
    private var listView: ListView? = null
    private lateinit var buttonAccount: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game_list)
        userId = intent.getStringExtra("userId")

        if (userId == null) {
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        showFavoritesOnly = false
        listView = findViewById(R.id.listView)
        val switchFavoritesOnly = findViewById<Switch>(R.id.switchFavoritesOnly)
        switchFavoritesOnly.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            showFavoritesOnly = isChecked
            updateListView()
        }

        buttonAccount = findViewById(R.id.buttonAccount)
        buttonAccount.setOnClickListener {
            val intent = Intent(this@GameListActivity, UserDetailsActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        updateListView()
    }

    private fun updateListView() {
        val gamesFlow: Flow<List<Game>> =
            if (showFavoritesOnly)
                gameProvider.fetchFavoriteGamesForUser(userId.orEmpty())
            else gameProvider.fetchAllGames()

        lifecycleScope.launch {
            gamesFlow.collect { games ->
                val adapter = GameListAdapter(this@GameListActivity, games, userId.orEmpty())
                listView?.adapter = adapter
            }
        }
    }
}