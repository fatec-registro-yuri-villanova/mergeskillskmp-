package com.fatec.merge_skills_kmp.features.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fatec.merge_skills_kmp.viewmodel.AuthViewModel

// Placeholder — implementação completa na Aula 14 (PainelCursos, PainelAulas, PainelQuestões)
@Composable
fun CmsScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "MergeSkills CMS",
            style = MaterialTheme.typography.headlineLarge
        )
        Button(onClick = {
            authViewModel.logout()
            onLogout()
        }) {
            Text("Sair")
        }
    }
}
