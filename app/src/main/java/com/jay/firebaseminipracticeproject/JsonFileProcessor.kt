package com.jay.firebaseminipracticeproject

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.jay.firebaseminipracticeproject.data.FormModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class JsonFileProcessor(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    fun processJsonFiles() {
        val assetManager = context.assets
        try {
            val jsonFiles = assetManager.list("") // Get a list of all files in the assets folder

            if (jsonFiles != null) {
                for (fileName in jsonFiles) {
                    if (fileName.endsWith(".json")) {
                        val inputStream = assetManager.open(fileName)
                        val reader = BufferedReader(InputStreamReader(inputStream))
                        val jsonString = reader.readText()

                        val gson = Gson()
                        val formData = gson.fromJson(jsonString, FormModel::class.java)

                        formData.formId=fileName

                        // Store the data in Firestore
                        firestore.collection("forms").document(fileName)
                            .set(formData)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {e->
                                e.printStackTrace()
                            }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}