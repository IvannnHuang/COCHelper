package com.cochelper.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cochelper.core.datastore.SettingsStore
import com.cochelper.core.datastore.UserSettings
import com.cochelper.core.model.ApiConfig
import com.cochelper.core.model.PlayerTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsForm(
    val playerTag: String = "",
    val apiToken: String = "",
    val baseUrl: String = "",
) {
    val tagError: Boolean get() = playerTag.isNotBlank() && !PlayerTag.isValid(playerTag)
    val urlError: Boolean get() = !baseUrl.trim().startsWith("https://")
    val canSave: Boolean get() = PlayerTag.isValid(playerTag) && apiToken.isNotBlank() && !urlError
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsStore: SettingsStore,
) : ViewModel() {

    private val _form = MutableStateFlow<SettingsForm?>(null)
    val form: StateFlow<SettingsForm?> = _form

    init {
        viewModelScope.launch {
            val current = settingsStore.settings.first()
            _form.value = SettingsForm(current.playerTag, current.apiToken, current.baseUrl)
        }
    }

    fun onTagChange(value: String) = _form.update { it?.copy(playerTag = value) }

    fun onTokenChange(value: String) = _form.update { it?.copy(apiToken = value) }

    fun onBaseUrlChange(value: String) = _form.update { it?.copy(baseUrl = value) }

    fun onResetBaseUrl() = onBaseUrlChange(ApiConfig.DEFAULT_BASE_URL)

    fun save(onSaved: () -> Unit) {
        val form = _form.value?.takeIf { it.canSave } ?: return
        viewModelScope.launch {
            settingsStore.save(
                UserSettings(
                    playerTag = PlayerTag.normalize(form.playerTag),
                    apiToken = form.apiToken.trim(),
                    baseUrl = form.baseUrl.trim().trimEnd('/'),
                ),
            )
            onSaved()
        }
    }
}
