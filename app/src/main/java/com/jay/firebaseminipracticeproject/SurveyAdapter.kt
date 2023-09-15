package com.jay.firebaseminipracticeproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jay.firebaseminipracticeproject.data.QuestionModel

class SurveyAdapter(
    fragmentActivity: FragmentActivity,
    private val questionModels: ArrayList<QuestionModel>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return questionModels.size
    }

    override fun createFragment(position: Int): Fragment {
        val questionModel = questionModels[position]
        return Questions.newInstance(questionModel)
    }

}