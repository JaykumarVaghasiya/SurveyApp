package com.jay.firebaseminipracticeproject.onBoarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment

import com.jay.firebaseminipracticeproject.R
import com.jay.firebaseminipracticeproject.userRegistration.Login

class OnBoarding3Fragment : Fragment(R.layout.fragment_on_boarding3) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val getStartedButton: Button = view.findViewById(R.id.btGetStarted)
        getStartedButton.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("onboarding_completed", true)
            editor.apply()

            val intent = Intent(requireActivity(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()

        }
    }
}