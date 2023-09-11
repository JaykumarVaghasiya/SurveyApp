package com.jay.firebaseminipracticeproject.userRegistration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.MainActivity
import com.jay.firebaseminipracticeproject.R
import com.jay.firebaseminipracticeproject.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationPage : AppCompatActivity() {

    private lateinit var registration: FirebaseAuth
    private val userCollectionPref = FirebaseFirestore.getInstance().collection("users")
    private lateinit var tIUserName: TextInputEditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)
        registration = FirebaseAuth.getInstance()

        tIUserName = findViewById(R.id.etUserName)
        btnRegister = findViewById(R.id.btRegister)
        tvLoginAccount = findViewById(R.id.tvLogin)


        btnRegister.setOnClickListener {
            registerUser()
        }

        tvLoginAccount.setOnClickListener {
            finish()
        }

    }

    private fun registerUser() {
        val tIEmail = findViewById<TextInputEditText>(R.id.etRegisterEmailId)
        val tIPassword = findViewById<TextInputEditText>(R.id.etRegisterPassword)

        val email = tIEmail.text.toString()
        val password = tIPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                registration.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this@RegistrationPage) {
                        registration = FirebaseAuth.getInstance()
                        val userName = tIUserName.text.toString()
                        val userId = registration.uid
                        val users = User(userId, userName)
                        saveUserName(users)
                        val intent = Intent(this@RegistrationPage, MainActivity::class.java)
                        intent.putExtra("userName", userName)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(
                            this@RegistrationPage,
                            "Failed to Register User",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

            }
        }
    }

    private fun saveUserName(user: User) = CoroutineScope(Dispatchers.IO).launch {
        userCollectionPref.document(user.id ?: "")
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(
                    this@RegistrationPage,
                    "Successfully Saved Data ",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }.addOnFailureListener { e ->
                Toast.makeText(this@RegistrationPage, e.message, Toast.LENGTH_SHORT).show()
            }
    }

}
