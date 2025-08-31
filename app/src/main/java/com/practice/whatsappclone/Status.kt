package com.practice.whatsappclone

data class Status(
    val statusId : String = "",
    val userId : String = "",
    val userName : String = "",
    val userProfileUrl : String = "",
    val imageUrl: String = "",
    val timeStamp : Long = 0L
)