package fr.esgi.taskflow.data.local

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(users: List<UserEntity>)
}