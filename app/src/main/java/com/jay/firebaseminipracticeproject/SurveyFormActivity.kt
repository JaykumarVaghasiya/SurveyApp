package com.jay.firebaseminipracticeproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore

class SurveyFormActivity : AppCompatActivity() {

    private lateinit var title: MaterialTextView
    private lateinit var desc: MaterialTextView
    private lateinit var questionSize: MaterialTextView
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey_form)

        title = findViewById(R.id.tvName)
        desc = findViewById(R.id.tvDesc)
        questionSize= findViewById(R.id.tvQuestionSize)

        getFormInfo()

        val btBack: MaterialButton = findViewById(R.id.btGoBack)
        val btStart: MaterialButton = findViewById(R.id.btGoNext)


        btBack.setOnClickListener {
            finish()
        }
        btStart.setOnClickListener {
            val intent= Intent(this,FillSurveyActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun getFormInfo() {

        val ref = db.collection("forms")
        ref.get().addOnSuccessListener {documentSnapShot->
            if(documentSnapShot != null) {

            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}