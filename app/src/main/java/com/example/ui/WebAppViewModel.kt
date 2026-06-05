package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.WebApp
import com.example.data.WebAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WebAppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WebAppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WebAppRepository(database.webAppDao)
    }

    val allApps: StateFlow<List<WebApp>> = repository.allApps
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedApp = MutableStateFlow<WebApp?>(null)
    val selectedApp: StateFlow<WebApp?> = _selectedApp.asStateFlow()

    fun selectApp(app: WebApp?) {
        _selectedApp.value = app
        if (app != null) {
            viewModelScope.launch {
                val updated = app.copy(
                    lastUsedTimestamp = System.currentTimeMillis(),
                    usageCount = app.usageCount + 1
                )
                repository.updateApp(updated)
            }
        }
    }

    fun insertApp(
        name: String,
        url: String,
        iconName: String,
        colorHex: String,
        notes: String,
        desktopMode: Boolean
    ) {
        viewModelScope.launch {
            val app = WebApp(
                name = name.trim().ifEmpty { "Apps Script WebApp" },
                url = formatUrl(url.trim()),
                iconName = iconName,
                colorHex = colorHex,
                notes = notes.trim(),
                desktopMode = desktopMode
            )
            repository.insertApp(app)
        }
    }

    fun insertAppWithCallback(
        name: String,
        url: String,
        onInserted: (WebApp) -> Unit
    ) {
        viewModelScope.launch {
            val app = WebApp(
                name = name.trim().ifEmpty { "Apps Script WebApp" },
                url = formatUrl(url.trim()),
                iconName = "language",
                colorHex = "#1A73E8",
                notes = "Aggiunta tramite avvio rapido link"
            )
            val generatedId = repository.insertApp(app)
            onInserted(app.copy(id = generatedId))
        }
    }

    fun updateApp(app: WebApp) {
        viewModelScope.launch {
            repository.updateApp(app.copy(url = formatUrl(app.url.trim())))
        }
    }

    fun deleteApp(app: WebApp) {
        viewModelScope.launch {
            repository.deleteApp(app)
        }
    }

    fun togglePin(app: WebApp) {
        viewModelScope.launch {
            repository.updateApp(app.copy(isPinned = !app.isPinned))
        }
    }

    private fun formatUrl(url: String): String {
        val trimmed = url.trim()
        return if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }
}
