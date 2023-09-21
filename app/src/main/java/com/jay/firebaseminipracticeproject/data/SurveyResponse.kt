package com.jay.firebaseminipracticeproject.data

import com.google.gson.annotations.SerializedName

data class SurveyResponse(
    @SerializedName("userId")
    var userId: String? = "",
    @SerializedName("formId")
    var formId: List<String> = mutableListOf(),
)
