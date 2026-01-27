package com.it2161.s243168t.movieviewer.data.repositories

import com.it2161.s243168t.movieviewer.data.local.daos.UserDao
import com.it2161.s243168t.movieviewer.data.local.models.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    fun getUserByIdFlow(id: Int): Flow<User?> {
        return userDao.getUserByIdFlow(id)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}
