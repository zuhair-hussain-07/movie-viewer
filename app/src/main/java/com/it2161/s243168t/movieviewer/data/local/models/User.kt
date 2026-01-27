package com.it2161.s243168t.movieviewer.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // internal id for database operations
    val userId: String, // like username (for login and registration)
    val dateOfBirth: Date,
    val password: String,
    val preferredName: String,
    val profilePicture: String? = null
)