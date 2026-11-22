package fr.esgi.taskflow.data.network

import fr.esgi.taskflow.domain.model.User

data class UserDto(val id: Int?, val name: String?, val email: String?)

fun UserDto.toDomainOrNull(): User? {
    val validId = id?.takeIf { it > 0 } ?: return null
    val validName = name?.takeIf { it.isNotBlank() } ?: return null
    return User(validId, validName, email.orEmpty())
}
