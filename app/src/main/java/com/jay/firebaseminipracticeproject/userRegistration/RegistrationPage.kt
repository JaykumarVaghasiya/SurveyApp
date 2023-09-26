package com.jay.firebaseminipracticeproject.userRegistration

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.MainActivity
import com.jay.firebaseminipracticeproject.R
import com.jay.firebaseminipracticeproject.data.SurveyResponse
import com.jay.firebaseminipracticeproject.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationPage : AppCompatActivity() {

    private lateinit var registration: FirebaseAuth
    private val userCollectionRef = FirebaseFirestore.getInstance().collection("users")
    private lateinit var tIUserName: TextInputEditText
    private lateinit var tILUserName: TextInputLayout
    private lateinit var btnRegister: Button
    private lateinit var tvLoginAccount:MaterialButton

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

        val userName = tIUserName.text.toString()
        val email = tIEmail.text.toString()
        val password = tIPassword.text.toString()

        tILEmail.error = null
        tILPassword.error = null
        tILUserName.error = null

        if (userName.isEmpty()) {
            tILUserName.error = getString(R.string.username_required)
            return
        }

        if (email.isEmpty()) {
            tILEmail.error = getString(R.string.email_required)
            return
        }

        if (password.isEmpty()) {
            tILPassword.error = getString(R.string.password_required)
            return
        }

        registration.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                val users = User(userId, userName, false)
                val response = SurveyResponse(userId, listOf())

                saveUserData(users, response)

                Toast.makeText(
                    this@RegistrationPage,
                    "User registered successfully",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@RegistrationPage, MainActivity::class.java)
                intent.putExtra("userName", users.userName).putExtra("id", userId)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this@RegistrationPage,
                    "Registration failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveUserData(user: User, response: SurveyResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            userCollectionRef.document(user.id ?: "")
                .set(user)
                .addOnSuccessListener {
                    val responseCollectionRef =
                        userCollectionRef.document(user.id ?: "").collection("response")
                    responseCollectionRef.document(response.formId.toString())
                        .set(response)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this@RegistrationPage,
                                R.string.user_data_saved,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@RegistrationPage,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this@RegistrationPage,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
