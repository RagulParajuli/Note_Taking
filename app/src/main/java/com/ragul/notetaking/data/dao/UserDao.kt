package com.ragul.notetaking.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.ragul.notetaking.data.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
