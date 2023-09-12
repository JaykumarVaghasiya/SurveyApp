package com.jay.firebaseminipracticeproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.jay.firebaseminipracticeproject.data.QuestionModel

class SurveyAdapter : RecyclerView.Adapter<SurveyAdapter.QuestionViewHolder>() {
    private val questionModels: ArrayList<QuestionModel> = arrayListOf()

    fun submitList(data: ArrayList<QuestionModel>) {
        questionModels.clear()
        questionModels.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_surveys, parent, false)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return QuestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val questionModel = questionModels[position]
        holder.bind(questionModel)
    }

    override fun getItemCount(): Int {
        return questionModels.size
    }

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionNumberTextView: MaterialTextView? = itemView.findViewById(R.id.tvQuestionNo)
        private val questionTextView: MaterialTextView? = itemView.findViewById(R.id.tvQuestion)


        fun bind(questionModel: QuestionModel) {
            questionNumberTextView?.text = "${questionModel.number}"
            questionTextView?.text = questionModel.question

        }
    }
}