package fr.esgi.taskflow.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: LoginUiState,
    onSelectUser: (Int) -> Unit,
    onLogin: () -> Unit,
    onRetry: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = state.users.firstOrNull { it.id == state.selectedUserId }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("TaskFlow", style = MaterialTheme.typography.headlineLarge)
        Text("Connexion de démonstration", modifier = Modifier.padding(bottom = 24.dp))
        Text("Choisir un utilisateur")
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = state.users.isNotEmpty()) {
                Text(selected?.name ?: "Sélectionner un utilisateur")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                state.users.forEach { user ->
                    DropdownMenuItem(
                        text = { Text("${user.name} (${user.email})") },
                        onClick = { onSelectUser(user.id); expanded = false },
                    )
                }
            }
        }
        if (state.loading) Text("Chargement des utilisateurs…")
        when (state.error) {
            LoginError.NETWORK -> Text("Impossible de charger les utilisateurs", color = MaterialTheme.colorScheme.error)
            LoginError.SELECTION -> Text("Choisissez un utilisateur valide", color = MaterialTheme.colorScheme.error)
            null -> Unit
        }
        TextButton(onClick = onRetry, enabled = !state.loading) { Text("Réessayer") }
        Button(
            onClick = onLogin,
            enabled = selected != null && !state.loading,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        ) { Text("Se connecter") }
    }
}
