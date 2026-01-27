package com.it2161.s243168t.movieviewer.data.repositories

import com.it2161.s243168t.movieviewer.data.SessionManager
import com.it2161.s243168t.movieviewer.data.local.daos.UserDao
import com.it2161.s243168t.movieviewer.data.local.models.User
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {

    suspend fun register(user: User): Boolean {
        if (userDao.getUserByUsername(user.userId) == null) {
            userDao.insertUser(user)
            return true
        }
        return false
    }

    suspend fun login(userId: String, password: String):Boolean {
        val user = userDao.getUserByUsername(userId)
        if (user != null && user.password == password) {
            sessionManager.saveSession(user.id.toString())
            return true
        }
        return false
    }

    suspend fun logout() {
        sessionManager.clearSession()
    }

    suspend fun getProfile(): User? {
        val userId = sessionManager.userId.firstOrNull()
        return if (userId != null) {
            userDao.getUserById(userId.toInt())
        } else {
            null
        }
    }
}