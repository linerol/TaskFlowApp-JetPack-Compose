package fr.esgi.taskflow.data.repository

import fr.esgi.taskflow.data.TaskRepository
import fr.esgi.taskflow.data.network.TaskApiService
import fr.esgi.taskflow.data.network.dto.toDomainOrNull
import fr.esgi.taskflow.data.network.toDomainOrNull
import fr.esgi.taskflow.domain.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RemoteTasksRepository(private val api: TaskApiService) : TaskRepository {
    private val lock = Any()
    private var remoteTasks: List<Task> = emptyList()
    private var sessionTasks: List<Task> = emptyList()
    private var completedOverrides: Map<Int, Boolean> = emptyMap()
    private var importantOverrides: Map<Int, Boolean> = emptyMap()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    override val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    override suspend fun refresh() {
        val fetched = api.getAllTodos().mapNotNull { it.toDomainOrNull() }
        synchronized(lock) {
            remoteTasks = fetched
            publish()
        }
    }

    private fun publish() {
        _tasks.value = (sessionTasks + remoteTasks).map { task ->
            task.copy(
                completed = completedOverrides[task.id] ?: task.completed,
                important = importantOverrides[task.id] ?: task.important,
            )
        }
    }

    override fun addTask(
        title: String,
        description: String,
        important: Boolean
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        synchronized(lock) {
            val nextId = (sessionTasks.minOfOrNull { it.id } ?: 0) - 1
            sessionTasks = sessionTasks + Task(
                id = nextId,
                title = cleanTitle,
                description = description.trim(),
                important = important,
            )
            publish()
        }
    }

    override fun setCompleted(taskId: Int, completed: Boolean) {
        synchronized(lock) {
            if (_tasks.value.none { it.id == taskId }) return
            completedOverrides = completedOverrides + (taskId to completed)
            publish()
        }
    }

    override fun setImportant(taskId: Int, important: Boolean) {
        synchronized(lock) {
            if (_tasks.value.none { it.id == taskId }) return
            importantOverrides = importantOverrides + (taskId to important)
            publish()
        }
    }
}
