package com.example.gamereview.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamereview.R
import com.example.gamereview.models.AppUser
import com.example.gamereview.providers.AuthProvider
import com.example.gamereview.providers.UserProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var favoriteGenreEditText: EditText
    private lateinit var favoriteGameEditText: EditText
    private lateinit var ageEditText: EditText

    private lateinit var registerButton: Button

    private val authProvider = AuthProvider()
    private val userProvider = UserProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        nameEditText = findViewById(R.id.nameEditText)
        favoriteGenreEditText = findViewById(R.id.favoriteGenreEditText)
        favoriteGameEditText = findViewById(R.id.favoriteGameEditText)
        ageEditText = findViewById(R.id.ageEditText)

        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val name = nameEditText.text.toString()
        val favoriteGenre = favoriteGenreEditText.text.toString()
        val favoriteGame = favoriteGameEditText.text.toString()
        val age = ageEditText.text.toString()

        val scope = CoroutineScope(Dispatchers.Main)

        if (email.isNotBlank() && password.isNotBlank()) {
            authProvider.createUser(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                        val user = AppUser("", task.result!!.uid, email, name, favoriteGenre, favoriteGame, age.toInt())

                        scope.launch {
                            try {
                                userProvider.addUser(user)
                                /*Toast.makeText(this@SignUpActivity, "User added successfully", Toast.LENGTH_SHORT)
                                    .show()*/
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    "Failed to add user: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)


                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }
}