package com.it2161.s243168t.movieviewer.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val id: Int,
    val adult: Boolean,
    val genres: List<String>,
    val originalLanguage: String,
    val title: String,
    val releaseDate: String,
    val runtime: Int,
    val voteCount: Int,
    val overview: String,
    val voteAverage: Double,
    val revenue: Long,
    val posterPath: String,
    val backdropPath: String,
    val category: String = ""
)
