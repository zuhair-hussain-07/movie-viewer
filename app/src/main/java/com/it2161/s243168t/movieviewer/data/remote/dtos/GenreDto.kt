package com.it2161.s243168t.movieviewer.data.remote.dtos

import com.google.gson.annotations.SerializedName

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
