package com.jay.firebaseminipracticeproject.data

import com.google.gson.annotations.SerializedName

data class QuestionModel(
    @SerializedName("number")
    var number: Int? = null,
    @SerializedName("question")
    var question: String? = null,
    @SerializedName("options")
    var options: ArrayList<String> = arrayListOf(),
    @SerializedName("answer")
    var answer: String? = null
)