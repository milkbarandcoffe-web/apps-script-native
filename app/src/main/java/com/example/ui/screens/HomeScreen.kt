package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WebApp

@Composable
fun getIconByName(name: String): ImageVector {
    return when (name) {
        "table_chart" -> Icons.Default.TableChart
        "calendar_today" -> Icons.Default.CalendarMonth
        "analytics" -> Icons.Default.Analytics
        "terminal" -> Icons.Default.Terminal
        "feed" -> Icons.Default.Feed
        "forum" -> Icons.Default.Forum
        "dashboard" -> Icons.Default.Dashboard
        "storage" -> Icons.Default.Storage
        else -> Icons.Default.Language
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    webApps: List<WebApp>,
    onAddApp: () -> Unit,
    onEditApp: (Long) -> Unit,
    onOpenApp: (WebApp) -> Unit,
    onTogglePin: (WebApp) -> Unit,
    onDeleteApp: (WebApp) -> Unit,
    onQuickLaunch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyPinned by remember { mutableStateOf(false) }
    var selectedAppForMenu by remember { mutableStateOf<WebApp?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<WebApp?>(null) }
    var showInstructionsDialog by remember { mutableStateOf(false) }

    val filteredApps = remember(webApps, searchQuery, showOnlyPinned) {
        webApps.filter { app ->
            val matchesSearch = app.name.contains(searchQuery, ignoreCase = true) || 
                                 app.url.contains(searchQuery, ignoreCase = true) ||
                                 app.notes.contains(searchQuery, ignoreCase = true)
            val matchesPin = !showOnlyPinned || app.isPinned
            matchesSearch && matchesPin
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Apps Script Hub",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Le tue web app in modalità nativa",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showInstructionsDialog = true },
                        modifier = Modifier.testTag("help_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Istruzioni Apps Script"
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddApp,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("add_app_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Aggiungi Web App")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aggiungi", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Filter controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cerca tra le tue Web App...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Cerca") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancella")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = showOnlyPinned,
                    onClick = { showOnlyPinned = !showOnlyPinned },
                    label = { Text("Preferite") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showOnlyPinned) Icons.Default.PushPin else Icons.Default.PinDrop,
                            contentDescription = "Filtro Preferiti",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.height(56.dp)
                )
            }

            // Quick App Launch Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("quick_launch_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Incolla URL",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Avvio rapido da link",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    var pastedUrl by remember { mutableStateOf("") }
                    var inputError by remember { mutableStateOf<String?>(null) }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = pastedUrl,
                            onValueChange = {
                                pastedUrl = it
                                inputError = null
                            },
                            placeholder = { Text("Incolla link https://script.google.com/macros/s/.../exec", fontSize = 12.sp) },
                            singleLine = true,
                            isError = inputError != null,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            trailingIcon = {
                                if (pastedUrl.isNotEmpty()) {
                                    IconButton(onClick = { pastedUrl = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Pulisci")
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("quick_url_input")
                        )

                        Button(
                            onClick = {
                                val urlToLaunch = pastedUrl.trim()
                                if (urlToLaunch.isEmpty()) {
                                    inputError = "Inserisci l'URL di Apps Script."
                                } else if (!urlToLaunch.startsWith("http://") && !urlToLaunch.startsWith("https://") && !urlToLaunch.contains("script.google.com")) {
                                    inputError = "L'indirizzo inserito non è valido."
                                } else {
                                    onQuickLaunch(urlToLaunch)
                                    pastedUrl = ""
                                    inputError = null
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp),
                            modifier = Modifier
                                .height(52.dp)
                                .testTag("quick_launch_button")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = "Apri")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apri", fontWeight = FontWeight.Bold)
                        }
                    }

                    if (inputError != null) {
                        Text(
                            text = inputError!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            if (filteredApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.Language,
                                contentDescription = "Nessuna web app trovata",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Nessun risultato trovato" else "Configura la tua prima Web App",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (searchQuery.isNotEmpty()) "Riprova con un'altra parola chiave" else "Puoi trasformare qualsiasi software creato con Google Apps Script in un'app desktop-mobile fluida compilando i parametri.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        if (searchQuery.isEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = onAddApp,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.CloudQueue, contentDescription = "Inizia ora")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Aggiungi la prima App")
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredApps, key = { it.id }) { app ->
                        val themeColor = remember(app.colorHex) {
                            try {
                                Color(android.graphics.Color.parseColor(app.colorHex))
                            } catch (e: Exception) {
                                Color(0xFF03A9F4)
                            }
                        }

                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(165.dp)
                                .combinedClickable(
                                    onClick = { onOpenApp(app) },
                                    onLongClick = { selectedAppForMenu = app }
                                )
                                .testTag("app_card_${app.id}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Dynamic theme color top brush
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(themeColor, themeColor.copy(alpha = 0.5f))
                                            )
                                        )
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp)
                                        .padding(top = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // App Custom Icon
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(
                                                    themeColor.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(10.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = getIconByName(app.iconName),
                                                contentDescription = app.name,
                                                tint = themeColor,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (app.isPinned) {
                                                Icon(
                                                    imageVector = Icons.Default.PushPin,
                                                    contentDescription = "Preferita",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            IconButton(
                                                onClick = { selectedAppForMenu = app },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = "Opzioni",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = app.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (app.notes.isNotEmpty()) {
                                        Text(
                                            text = app.notes,
                                            fontSize = 12.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "ID: ${app.id} • ${if (app.desktopMode) "PC" else "Smart"}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Text(
                                            text = "${app.usageCount} utilizzi",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                        )

                                        Icon(
                                            imageVector = Icons.Default.ArrowOutward,
                                            contentDescription = "Launch",
                                            tint = themeColor.copy(alpha = 0.8f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Options Modal Menu
    selectedAppForMenu?.let { app ->
        val onDismiss = { selectedAppForMenu = null }
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(app.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = app.url,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ListItem(
                        headlineContent = { Text(if (app.isPinned) "Rimuovi dai preferiti" else "Aggiungi ai preferiti") },
                        leadingContent = {
                            Icon(
                                imageVector = if (app.isPinned) Icons.Default.PushPin else Icons.Default.PinDrop,
                                contentDescription = "Pin"
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                onTogglePin(app)
                                onDismiss()
                            }
                            .clip(RoundedCornerShape(8.dp))
                    )

                    ListItem(
                        headlineContent = { Text("Modifica Web App") },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = "Modifica") },
                        modifier = Modifier
                            .clickable {
                                onEditApp(app.id)
                                onDismiss()
                            }
                            .clip(RoundedCornerShape(8.dp))
                    )

                    ListItem(
                        headlineContent = { Text("Elimina", color = MaterialTheme.colorScheme.error) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Elimina",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                showDeleteConfirmDialog = app
                                onDismiss()
                            }
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("Chiudi")
                }
            }
        )
    }

    // Delete confirmation dialog
    showDeleteConfirmDialog?.let { app ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminare ${app.name}?") },
            text = { Text("Sei sicuro di voler rimuovere questa web app? Questa azione cancellerà anche le preferenze e lo zoom impostati localmente.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteApp(app)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Elimina", color = MaterialTheme.colorScheme.onError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Annulla")
                }
            }
        )
    }

    // Instructions Dialog
    if (showInstructionsDialog) {
        AlertDialog(
            onDismissRequest = { showInstructionsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Informazioni",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Come configurare", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Segui questi passaggi all'interno del portale Google Apps Script per abilitare l'app:",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "1. Apri il tuo codice nel pannello **script.google.com**.\n" +
                                "2. Clicca sul pulsante **Includi (Deploy)** in alto a destra.\n" +
                                "3. Dal menu a comparsa, inserisci **Nuova installazione (New deployment)**.\n" +
                                "4. Scegli il tipo di installazione impostando **Applicazione Web (Web App)**.\n" +
                                "5. imposta **Esegui come (Execute as)**: *Tu (Your account)*.\n" +
                                "6. Imposta **Chi ha accesso (Who has access)**: *Chiunque (Anyone)*. Questo passaggio è necessario per consentire all'app di instradare correttamente le autenticazioni e consentirti l'accesso tramite login Google in WebView.\n" +
                                "7. Clicca su **Includi**, copia l'URL fornito (deve terminare con `/exec`) e incollalo qui in alto cliccando sul tasto Aggiungi.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showInstructionsDialog = false }) {
                    Text("Ho capito")
                }
            }
        )
    }
}
