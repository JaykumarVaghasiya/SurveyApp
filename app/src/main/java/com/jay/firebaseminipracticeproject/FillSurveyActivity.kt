package com.jay.firebaseminipracticeproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.FormModel
import com.jay.firebaseminipracticeproject.data.QuestionModel

class FillSurveyActivity : AppCompatActivity(), Questions.FormSurveyListener {

    private lateinit var viewPager2: ViewPager2
    private lateinit var pagerAdapter: SurveyAdapter
    private lateinit var currentUser: FirebaseUser
    var formId = ""


    lateinit var questions: ArrayList<QuestionModel>
    private lateinit var submit: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_survey)

        formId = intent.getStringExtra("form").toString()
        val db = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser!!

        viewPager2 = findViewById(R.id.viewPager2)
        submit = findViewById(R.id.btSubmit)

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == questions.size - 1) {
                    submit.visibility = View.VISIBLE
                } else {
                    submit.visibility = View.GONE
                }
            }

        })

        db.collection("surveyResponse")
            .whereEqualTo("userId", currentUser.uid)
            .whereEqualTo("formId", formId)
            .get()
            .addOnSuccessListener { querySnapshot ->

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }


        db.collection("forms")
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (document in querySnapshot) {
                    val formModel = document.toObject(FormModel::class.java)
                    if (formId == formModel.formId) {
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

        submit.setOnClickListener {
            updateAnswerInFirestore()

            finish()
        }


    }

    override fun onSubmitClick(questionModel: QuestionModel) {
        val index = questions.indexOfFirst { it.number == questionModel.number }
        if (index != -1) {
            questions[index] = questionModel
        }
    }

    private fun updateAnswerInFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        if (formId.isNotEmpty()) {
            firestore.collection("forms").document(formId)
                .update("questions", questions)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Answer updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }.addOnFailureListener { e ->

                    Toast.makeText(
                        this,
                        "Error updating answer: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

}
