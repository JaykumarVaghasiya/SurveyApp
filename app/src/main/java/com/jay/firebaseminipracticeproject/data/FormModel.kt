package com.jay.firebaseminipracticeproject.data

import com.google.gson.annotations.SerializedName

data class FormModel(

    @SerializedName("title")
    var title: String? = "",
    @SerializedName("userId")
    var userId: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("formId")
    var formId: String? = "",
    @SerializedName("status")
    var status: FormStatus? = FormStatus.PENDING,
    @SerializedName("questions")
    var questions: ArrayList<QuestionModel> = arrayListOf()
)


