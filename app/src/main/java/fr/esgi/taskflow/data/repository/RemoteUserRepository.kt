package fr.esgi.taskflow.data.repository

import fr.esgi.taskflow.data.UserRepository
import fr.esgi.taskflow.data.network.TaskApiService
import fr.esgi.taskflow.data.network.toDomainOrNull
import fr.esgi.taskflow.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RemoteUserRepository(private val api: TaskApiService) : UserRepository {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    override val users: StateFlow<List<User>> = _users.asStateFlow()

    override suspend fun refresh(): List<User> {
        val fetched = api.getUsers().mapNotNull { it.toDomainOrNull() }
        _users.value = fetched
        return fetched
    }
}
