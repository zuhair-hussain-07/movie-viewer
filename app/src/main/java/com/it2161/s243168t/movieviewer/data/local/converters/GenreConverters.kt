package com.it2161.s243168t.movieviewer.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class GenreConverters {
    @TypeConverter
    fun fromGenresList(genres: List<String>?): String? {
        return genres?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toGenresList(genresJson: String?): List<String>? {
        return genresJson?.let { Gson().fromJson(it, Array<String>::class.java).toList() }
    }
}
