package com.jay.firebaseminipracticeproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.QuestionModel

class FillSurveyActivity : AppCompatActivity() {

    private lateinit var adapter: SurveyAdapter
    private lateinit var viewPager2: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_survey)

        val db = FirebaseFirestore.getInstance()

        viewPager2 = findViewById(R.id.viewPager2)
        adapter = SurveyAdapter()
        viewPager2.adapter=adapter

        db.collection("forms")
            .get()
            .addOnSuccessListener { querySnapshot ->

                val questionsList = arrayListOf<QuestionModel>()

                for (document in querySnapshot) {
                    val questionData = document.toObject(QuestionModel::class.java)
                    questionsList.add(questionData)
                }
                adapter.submitList(questionsList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
    }


}
