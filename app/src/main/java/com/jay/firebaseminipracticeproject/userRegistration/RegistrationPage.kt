package com.jay.firebaseminipracticeproject.userRegistration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
    private lateinit var tILUserName: TextInputLayout
    private lateinit var btnRegister: Button
    private lateinit var tvLoginAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)
        registration = FirebaseAuth.getInstance()
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

        val tILEmail = findViewById<TextInputLayout>(R.id.tILEmail)
        val tILPassword = findViewById<TextInputLayout>(R.id.tILPassword)

        tILUserName = findViewById(R.id.tILUserName)
        tIUserName = findViewById(R.id.etUserName)

        val userName=tIUserName.text.toString()
        val email = tIEmail.text.toString()
        val password = tIPassword.text.toString()

        tILEmail.error = null
        tILPassword.error = null
        tILUserName.error = null

        if (userName.isEmpty()) {
            tILUserName.error = getString(R.string.username_required)
        }

        if (email.isEmpty()) {
            tILEmail.error = getString(R.string.email_required)
        }

        if (password.isEmpty()) {
            tILPassword.error =getString(R.string.password_required)
        }

        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                registration.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(this@RegistrationPage) {
                        registration = FirebaseAuth.getInstance()
                        val userId = registration.uid
                        val users = User(userId, userName,false)
                        saveUserName(users)
                        Toast.makeText(
                            this@RegistrationPage,
                            it.toString(),
                            Toast.LENGTH_SHORT)
                        val intent = Intent(this@RegistrationPage, MainActivity::class.java)
                        intent.putExtra("userName", users.userName).putExtra("id", userId)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener {e->
                        Toast.makeText(
                            this@RegistrationPage,
                            e.message,
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
                    R.string.successfully_saved_data,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }.addOnFailureListener { e ->
                Toast.makeText(this@RegistrationPage, e.message, Toast.LENGTH_SHORT).show()
            }
    }

}
