package com.example.gamereview.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gamereview.R
import com.example.gamereview.models.AppUser
import com.example.gamereview.providers.AuthProvider
import com.example.gamereview.providers.UserProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var user: AppUser
    private lateinit var nameEditText: EditText
    private lateinit var favoriteGenreEditText: EditText
    private lateinit var favoriteGameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var signOutButton: Button

    private lateinit var authProvider: AuthProvider
    private lateinit var userProvider: UserProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        nameEditText = findViewById(R.id.nameEditText)
        favoriteGenreEditText = findViewById(R.id.favoriteGenreEditText)
        favoriteGameEditText = findViewById(R.id.favoriteGameEditText)
        ageEditText = findViewById(R.id.ageEditText)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
        signOutButton = findViewById(R.id.signOutButton)

        authProvider = AuthProvider()
        userProvider = UserProvider()

        val userId = intent.getStringExtra("userId")

        if (userId == null) {
            Toast.makeText(this, "User ID not available", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            loadData(userId)
        }


        saveButton.setOnClickListener {
            val updatedUser = AppUser(
                user.documentId,
                userId,
                user.email,
                nameEditText.text.toString(),
                favoriteGenreEditText.text.toString(),
                favoriteGameEditText.text.toString(),
                ageEditText.text.toString().toIntOrNull() ?: user.age
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    updateUser(updatedUser)
                    showToast("User is updated.", true)
                } catch (e: Exception) {
                    showToast("Can not update user.", false)
                }
            }
        }

        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    deleteUser(userId)
                    deleteCurrentUser()

                    val signInIntent = Intent(this@UserDetailsActivity, SignInActivity::class.java)
                    startActivity(signInIntent)
                    finish()
                } catch (e: Exception) {
                    showToast("Can not delete user.", false)
                }
            }
        }

        signOutButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    signOut()

                    val signInIntent = Intent(this@UserDetailsActivity, SignInActivity::class.java)
                    startActivity(signInIntent)
                    finish()
                } catch (e: Exception) {
                    showToast("Can not sign out.", false)
                }
            }
        }
    }

    private suspend fun loadData(userId: String) {
        user = userProvider.loadUser(userId)!!

        nameEditText.setText(user.name)
        favoriteGenreEditText.setText(user.favoriteGenre)
        favoriteGameEditText.setText(user.favoriteGame)
        ageEditText.setText(user.age.toString())
    }

    private suspend fun updateUser(updatedUser: AppUser) {
        userProvider.updateUser(updatedUser)
        user = updatedUser
    }

    private suspend fun deleteUser(userId: String) {
        userProvider.deleteUser(userId)
    }

    private fun deleteCurrentUser() {
        authProvider.deleteUser()
    }

    private fun signOut() {
        authProvider.signOut()
    }

    private fun showToast(message: String, isSuccess: Boolean) {
        val color = if (isSuccess) Color.GREEN else Color.RED

        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val toastLayout = toast.view as? LinearLayout

        toastLayout?.let {
            val toastTextView = it.findViewById<TextView>(android.R.id.message)
            toastTextView?.setBackgroundResource(android.R.color.transparent)
            toastTextView?.setTextColor(Color.WHITE)
            toastLayout.setBackgroundColor(color)
        }

        toast.show()
    }
}