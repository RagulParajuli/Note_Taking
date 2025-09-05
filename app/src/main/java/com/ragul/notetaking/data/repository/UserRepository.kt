package com.ragul.notetaking.data.repository

import com.ragul.notetaking.data.dao.UserDao
import com.ragul.notetaking.data.model.User

class UserRepository(private val userDao: UserDao) {
    suspend fun upsert(user: User) = userDao.upsert(user)
    suspend fun getCurrentUser(): User? = userDao.getCurrentUser()
    suspend fun clearAll() = userDao.clearAll()
}
