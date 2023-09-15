package com.jay.firebaseminipracticeproject.userRegistration

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.jay.firebaseminipracticeproject.MainActivity
import com.jay.firebaseminipracticeproject.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val login = findViewById<MaterialButton>(R.id.btLogin)
        val createAccount = findViewById<MaterialButton>(R.id.tvCreateAccount)

        if (auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        login.setOnClickListener {
            loginUser()
        }

        createAccount.setOnClickListener {
            val intent = Intent(this, RegistrationPage::class.java)
            startActivity(intent)
        }

    }

    private fun loginUser() {
        val loginEmail = findViewById<TextInputEditText>(R.id.etLoginEmailId)
        val loginEmailLayout=findViewById<TextInputLayout>(R.id.tILLoginEmail)
        val loginPassword = findViewById<TextInputEditText>(R.id.etLoginPassword)
        val loginPasswordLayout=findViewById<TextInputLayout>(R.id.tILLoginPassword)

        val email = loginEmail.text.toString()
        val password = loginPassword.text.toString()

        loginEmailLayout.error = null
        loginPasswordLayout.error = null

        if (email.isEmpty()) {
            loginEmailLayout.error = getString(R.string.email_required)
        }

        if (password.isEmpty()) {
            loginPasswordLayout.error = getString(R.string.password_required)
        }


        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this@Login) {
                        val intent = Intent(this@Login, MainActivity::class.java)
                        Toast.makeText(this@Login, R.string.successfully_Login, Toast.LENGTH_LONG)
                            .show()
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@Login,
                            R.string.email_password_incorrect,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

}
