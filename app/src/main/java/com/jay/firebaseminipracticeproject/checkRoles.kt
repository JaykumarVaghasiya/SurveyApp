package com.jay.firebaseminipracticeproject

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jay.firebaseminipracticeproject.data.User

fun checkRoles(context: Context, auth: FirebaseAuth,addSurvey:MaterialButton) {

    val id = auth.uid
    val db = FirebaseFirestore.getInstance()
    if (id != null) {
        db.collection("users").document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(User::class.java)
                    if (userData?.admin == true) {
                        addSurvey.visibility = View.VISIBLE
                    } else {
                        addSurvey.visibility = View.GONE
                    }
                } else {
                    db.collection("forms")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
            }
    }
}