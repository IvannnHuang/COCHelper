package com.cochelper.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SettingsScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val form by viewModel.form.collectAsStateWithLifecycle()
    val current = form ?: return
    var showToken by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = current.playerTag,
            onValueChange = viewModel::onTagChange,
            label = { Text("Player tag") },
            placeholder = { Text("#GVJGG2JQV") },
            singleLine = true,
            isError = current.tagError,
            supportingText = {
                Text(
                    if (current.tagError) "Tags only use 0 2 8 9 P Y L Q G R J C U V" else "In the game: tap your name → the tag under it",
                )
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = current.apiToken,
            onValueChange = viewModel::onTokenChange,
            label = { Text("API token") },
            singleLine = true,
            visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { showToken = !showToken }) { Text(if (showToken) "Hide" else "Show") }
            },
            supportingText = { Text("From developer.clashofclans.com → My Account. Allowed IP: 45.79.218.79") },
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = current.baseUrl,
            onValueChange = viewModel::onBaseUrlChange,
            label = { Text("API base URL") },
            singleLine = true,
            isError = current.urlError,
            supportingText = { Text(if (current.urlError) "Must start with https://" else "Default is the RoyaleAPI proxy") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(onClick = viewModel::onResetBaseUrl) { Text("Reset URL to default") }

        Button(
            onClick = { viewModel.save(onDone) },
            enabled = current.canSave,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Save")
        }
    }
}
