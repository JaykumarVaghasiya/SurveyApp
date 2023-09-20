package com.jay.firebaseminipracticeproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.QuestionModel

class Questions : Fragment() {
    internal lateinit var questionModel: QuestionModel
    private var isLastFragment: Boolean = false
    private lateinit var firestore: FirebaseFirestore

    companion object {
        fun newInstance(
            questionModel: QuestionModel
        ): Questions {
            val fragment = Questions()
            fragment.questionModel = questionModel
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_questions, container, false)

        val questionNumberTextView: MaterialTextView =
            view.findViewById(R.id.tvQuestionNo)
        val questionTextView: MaterialTextView = view.findViewById(R.id.tvQuestion)
        val radioBTOptions: RadioGroup = view.findViewById(R.id.rgOptions)
        val submit: MaterialButton = view.findViewById(R.id.btSubmit)


        questionNumberTextView.text = questionModel.number.toString()
        questionTextView.text = questionModel.question

        for (option in questionModel.options) {

            val radioButton = RadioButton(radioBTOptions.context)
            radioButton.id = View.generateViewId()
            radioButton.text = option
            radioBTOptions.addView(radioButton)
            if (option == questionModel.answer) {
                radioButton.isChecked = true
            }

        }
        radioBTOptions.setOnCheckedChangeListener { _, checkedId ->

            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            val selectedText = selectedRadioButton?.text
            questionModel.answer = selectedText.toString()

        }

        submit.setOnClickListener {
            updateAnswerInFirestore()
        }

        isLastFragment = isLastFragment()
        submit.visibility = if (isLastFragment) View.VISIBLE else View.GONE
        return view
    }

    private fun isLastFragment(): Boolean {
        return true
    }
    private fun updateAnswerInFirestore() {

        val documentRef = firestore.collection("forms").document(questionModel.answer!!)


        documentRef.update("answers.${questionModel.number}", questionModel.answer)
            .addOnSuccessListener {

                Toast.makeText(requireContext(), "Answer updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(requireContext(), "Error updating answer: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}