package com.jay.firebaseminipracticeproject.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FormModel(

    @SerializedName("title")
    var title: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("formId")
    var formId: String? = null,
    @SerializedName("status")
    var status: String? = "PENDING",
    @SerializedName("questions")
    var questions: ArrayList<QuestionModel> = arrayListOf()
):Serializable


