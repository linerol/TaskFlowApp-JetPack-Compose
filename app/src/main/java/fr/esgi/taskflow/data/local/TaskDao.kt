package fr.esgi.taskflow.data.local

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsert(tasks: List<TaskEntity>)
}