package com.example.gamereview.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gamereview.R
import com.example.gamereview.providers.AuthProvider

class SignInActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button
    private lateinit var goToSignUpButton: Button

    private val authProvider = AuthProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.signInButton)
        goToSignUpButton = findViewById(R.id.goToSignUpButton)

        signInButton.setOnClickListener { login() }
        goToSignUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        authProvider.signIn(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, GameListActivity::class.java).apply {
                        putExtra("userId", task.result?.uid)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, task.exception?.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}