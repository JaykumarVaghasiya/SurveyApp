package com.jay.firebaseminipracticeproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.data.QuestionModel

class FillSurveyActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var pagerAdapter: SurveyAdapter
    private lateinit var questions: ArrayList<QuestionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_survey)
        val id = intent.getStringExtra("form")
        val db = FirebaseFirestore.getInstance()

        viewPager2 = findViewById(R.id.viewPager2)


        db.collection("forms")
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (document in querySnapshot) {
                    val formModel = document.toObject(FormModel::class.java)
                    if (id == formModel.formId) {
                        questions = formModel.questions
                    }
                }

                pagerAdapter = SurveyAdapter(
                    this,
                    questions
                )
                viewPager2.adapter = pagerAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
    }

}