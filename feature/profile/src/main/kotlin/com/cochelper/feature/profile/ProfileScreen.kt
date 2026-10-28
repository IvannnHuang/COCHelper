package com.cochelper.feature.profile

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cochelper.core.data.CachedPlayer
import com.cochelper.core.model.Player
import com.cochelper.core.network.FetchError

@Composable
internal fun ProfileScreen(
    onEditSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileContent(state, viewModel::onPullToRefresh, onEditSettings, modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onRefresh: () -> Unit,
    onEditSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = state.refreshing && state.cached != null,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        val cached = state.cached
        when {
            cached == null && (state.loading || state.refreshing) -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            cached == null -> LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item { ErrorCard(state.error ?: FetchError.UNKNOWN, hasCache = false, onEditSettings) }
            }
            else -> PlayerOverview(cached, state.error, onEditSettings)
        }
    }
}

@Composable
private fun PlayerOverview(cached: CachedPlayer, error: FetchError?, onEditSettings: () -> Unit) {
    val player = cached.player
    val heroes = player.heroes.filter { it.village == "home" }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (error != null) item { ErrorCard(error, hasCache = true, onEditSettings) }
        item { Header(player, cached.fetchedAtMillis, onEditSettings) }
        item { Stats(player) }
        if (heroes.isNotEmpty()) {
            item { SectionTitle("Heroes") }
            items(heroes, key = { it.name }) { hero ->
                Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(hero.name, style = MaterialTheme.typography.bodyLarge)
                    Text("Lv ${hero.level}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        item { SectionTitle("Collection") }
        item {
            val troops = player.troops.count { it.village == "home" }
            Text(
                "$troops home troops · ${player.spells.size} spells · ${player.heroEquipment.size} hero equipment",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun Header(player: Player, fetchedAtMillis: Long, onEditSettings: () -> Unit) {
    Column {
        Text(player.name, style = MaterialTheme.typography.headlineMedium)
        Text(
            "${player.tag} · Town Hall ${player.townHallLevel} · XP ${player.expLevel}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            val now = System.currentTimeMillis()
            Text(
                if (now - fetchedAtMillis < DateUtils.MINUTE_IN_MILLIS) {
                    "Updated just now"
                } else {
                    "Updated " + DateUtils.getRelativeTimeSpanString(fetchedAtMillis, now, DateUtils.MINUTE_IN_MILLIS)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onEditSettings) { Text("Settings") }
        }
    }
}

@Composable
private fun Stats(player: Player) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Trophies", player.trophies.toString(), Modifier.weight(1f))
        StatCard("War stars", player.warStars.toString(), Modifier.weight(1f))
        StatCard("Clan", player.clan?.name ?: "None", Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
}

@Composable
private fun ErrorCard(error: FetchError, hasCache: Boolean, onEditSettings: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(error.message(), color = MaterialTheme.colorScheme.onErrorContainer)
            if (hasCache) {
                Text(
                    "Showing your last saved profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
            if (error.isSettingsProblem) {
                OutlinedButton(onClick = onEditSettings) { Text("Open settings") }
            }
        }
    }
}
