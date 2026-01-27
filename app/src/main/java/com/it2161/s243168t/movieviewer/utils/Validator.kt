package com.it2161.s243168t.movieviewer.utils

object Validators {

    fun isDateOfBirthInPast(dob: Long?): Boolean {
        if (dob == null) return false
        return dob < System.currentTimeMillis()
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isValidProfilePictureFormat(uri: String?): Boolean {
        if (uri == null) return true // Profile picture is optional
        val lowerCaseUri = uri.lowercase()
        return lowerCaseUri.endsWith(".jpg") ||
                lowerCaseUri.endsWith(".jpeg") ||
                lowerCaseUri.endsWith(".png") ||
                lowerCaseUri.startsWith("content://") ||
                lowerCaseUri.startsWith("file://")
    }
}
