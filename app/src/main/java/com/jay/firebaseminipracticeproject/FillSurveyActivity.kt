package com.jay.firebaseminipracticeproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class FillSurveyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_survey)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager2)

    }
}