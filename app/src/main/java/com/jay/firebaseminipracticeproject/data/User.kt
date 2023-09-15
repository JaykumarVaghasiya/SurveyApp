package com.jay.firebaseminipracticeproject.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id:String?=null,
    @SerializedName("userName")
    val userName: String="",
    @SerializedName("admin")
    val admin: Boolean=false
)
