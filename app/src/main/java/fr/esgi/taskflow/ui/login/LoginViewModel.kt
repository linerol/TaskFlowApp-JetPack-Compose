package fr.esgi.taskflow.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import fr.esgi.taskflow.data.UserRepository
import fr.esgi.taskflow.domain.model.User
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginError { NETWORK, SELECTION }

data class LoginUiState(
    val users: List<User> = emptyList(),
    val selectedUserId: Int? = null,
    val loggedInUser: User? = null,
    val loading: Boolean = false,
    val error: LoginError? = null,
)

class LoginViewModel(private val users: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow(LoginUiState())
    val state = _state.asStateFlow()

    init { loadUsers() }

    fun loadUsers() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val fetched = users.refresh()
                _state.update { it.copy(users = fetched, loading = false) }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _state.update { it.copy(loading = false, error = LoginError.NETWORK) }
            }
        }
    }

    fun selectUser(id: Int) {
        _state.update { it.copy(selectedUserId = id, error = null) }
    }

    fun login() {
        val selectedId = state.value.selectedUserId ?: return
        val chosen = state.value.users.firstOrNull { it.id == selectedId }
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                _state.update {
                    it.copy(
                        users = state.value.users,
                        loading = false,
                        loggedInUser = chosen,
                        error = if (chosen == null) LoginError.SELECTION else null,
                    )
                }
            } catch (error: Exception) {
                if (error is CancellationException) throw error
                _state.update { it.copy(loading = false, error = LoginError.NETWORK) }
            }
        }
    }
}

class LoginViewModelFactory(private val users: UserRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(LoginViewModel::class.java))
        return LoginViewModel(users) as T
    }
}
