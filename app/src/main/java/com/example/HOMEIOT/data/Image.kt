package com.example.HOMEIOT.data


import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("documents")
    val documents: List<Document>,
    @SerializedName("meta")
    val meta: Meta
)