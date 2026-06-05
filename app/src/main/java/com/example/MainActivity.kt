package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WebApp
import com.example.ui.WebAppViewModel
import com.example.ui.screens.AddEditScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ViewerScreen
import com.example.ui.theme.MyApplicationTheme

sealed interface Screen {
    object Home : Screen
    data class AddEdit(val appId: Long?) : Screen
    data class Viewer(val app: WebApp) : Screen
}

class MainActivity : ComponentActivity() {
    private val viewModel: WebAppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                    val webApps by viewModel.allApps.collectAsStateWithLifecycle()

                    BackHandler(enabled = currentScreen != Screen.Home) {
                        currentScreen = Screen.Home
                    }

                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    webApps = webApps,
                                    onAddApp = { currentScreen = Screen.AddEdit(null) },
                                    onEditApp = { id -> currentScreen = Screen.AddEdit(id) },
                                    onOpenApp = { app ->
                                        viewModel.selectApp(app)
                                        currentScreen = Screen.Viewer(app)
                                    },
                                    onTogglePin = { app -> viewModel.togglePin(app) },
                                    onDeleteApp = { app -> viewModel.deleteApp(app) },
                                    onQuickLaunch = { url ->
                                        viewModel.insertAppWithCallback(
                                            name = "Apps Script Autosalvata",
                                            url = url
                                        ) { insertedApp ->
                                            viewModel.selectApp(insertedApp)
                                            currentScreen = Screen.Viewer(insertedApp)
                                        }
                                    }
                                )
                            }
                            is Screen.AddEdit -> {
                                val appToEdit = screen.appId?.let { id ->
                                    webApps.find { it.id == id }
                                }
                                AddEditScreen(
                                    appToEdit = appToEdit,
                                    onSave = { name, url, icon, color, notes, desktop, zoom ->
                                        if (appToEdit == null) {
                                            viewModel.insertApp(name, url, icon, color, notes, desktop)
                                        } else {
                                            viewModel.updateApp(
                                                appToEdit.copy(
                                                    name = name,
                                                    url = url,
                                                    iconName = icon,
                                                    colorHex = color,
                                                    notes = notes,
                                                    desktopMode = desktop,
                                                    textZoom = zoom
                                                )
                                            )
                                        }
                                        currentScreen = Screen.Home
                                    },
                                    onBack = { currentScreen = Screen.Home }
                                )
                            }
                            is Screen.Viewer -> {
                                ViewerScreen(
                                    app = screen.app,
                                    onBack = { currentScreen = Screen.Home }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
