package com.jay.firebaseminipracticeproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textview.MaterialTextView
import com.jay.firebaseminipracticeproject.data.QuestionModel

class Questions : Fragment() {
    internal lateinit var questionModel: QuestionModel

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
            optionListener?.onSubmitClick(questionModel)

        }
        return view
    }

    private var optionListener: FormSurveyListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FormSurveyListener) {
            optionListener = context
        } else {
            throw RuntimeException("$context must implement FormSurveyListener")
        }
    }

    interface FormSurveyListener {
        fun onSubmitClick(questionModel: QuestionModel)
    }
}





