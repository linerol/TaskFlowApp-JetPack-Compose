package fr.esgi.taskflow.data

import fr.esgi.taskflow.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val users: StateFlow<List<User>>
    suspend fun refresh(): List<User>
}
