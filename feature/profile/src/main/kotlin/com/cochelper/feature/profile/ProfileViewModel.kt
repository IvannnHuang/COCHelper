package com.cochelper.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cochelper.core.data.CachedPlayer
import com.cochelper.core.data.PlayerRepository
import com.cochelper.core.data.RefreshResult
import com.cochelper.core.datastore.SettingsStore
import com.cochelper.core.network.FetchError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val loading: Boolean = true,
    val cached: CachedPlayer? = null,
    val refreshing: Boolean = false,
    val error: FetchError? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: PlayerRepository,
    settingsStore: SettingsStore,
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)
    private val error = MutableStateFlow<FetchError?>(null)

    val uiState: StateFlow<ProfileUiState> = combine(repository.player, refreshing, error) { cached, refreshing, error ->
        ProfileUiState(loading = false, cached = cached, refreshing = refreshing, error = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    init {
        // Refresh on open (cache permitting), and force a refresh whenever the settings are edited.
        viewModelScope.launch {
            settingsStore.settings.distinctUntilChanged().withIndex().collectLatest { (index, _) ->
                refresh(force = index > 0)
            }
        }
    }

    fun onPullToRefresh() {
        viewModelScope.launch { refresh(force = true) }
    }

    private suspend fun refresh(force: Boolean) {
        refreshing.value = true
        error.value = (repository.refresh(force) as? RefreshResult.Failed)?.error
        refreshing.value = false
    }
}
