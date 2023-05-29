package com.beyzasker.housemateapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var backButtonPressedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (checkLoggedInState()) {
            val loggedInState = Intent(this@LoginActivity, HomePageActivity::class.java)
            finish()
            startActivity(loggedInState)
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            signIn(email, password)
        }

        signUpButton.setOnClickListener {
            val signUpIntent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (++backButtonPressedCount == 2) {
            finishAffinity()
        }
    }

    private fun checkLoggedInState(): Boolean {
        val user = auth.currentUser
        return user != null && user.isEmailVerified
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    if(!user!!.isEmailVerified) {
                        auth.signOut()
                        return@addOnCompleteListener
                    }
                    val loginIntent = Intent(this@LoginActivity, HomePageActivity::class.java)
                    loginIntent.putExtra("userId", user!!.uid)
                    loginIntent.putExtra("displayName", user.displayName)
                    startActivity(loginIntent)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        // [END sign_in_with_email]
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}