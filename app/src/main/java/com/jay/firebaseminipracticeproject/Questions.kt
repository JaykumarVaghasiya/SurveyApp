package com.jay.firebaseminipracticeproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class Questions : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_questions, container, false)

        val question=view.findViewById<MaterialTextView>(R.id.tvQuestion)
        val questionNo=view.findViewById<MaterialTextView>(R.id.tvQuestionNo)
        val radioGroup=view.findViewById<RadioGroup>(R.id.rgOptions)
        val btBack: MaterialButton=view.findViewById(R.id.btBack)
        val btSubmit: MaterialButton=view.findViewById(R.id.btSubmit)


        btBack.setOnClickListener {

        }

        btSubmit.setOnClickListener {

        }




        return view


    }


}