package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WebApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    appToEdit: WebApp?,
    onSave: (name: String, url: String, iconName: String, colorHex: String, notes: String, desktopMode: Boolean, textZoom: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(appToEdit?.name ?: "") }
    var url by remember { mutableStateOf(appToEdit?.url ?: "") }
    var selectedIcon by remember { mutableStateOf(appToEdit?.iconName ?: "language") }
    var selectedColor by remember { mutableStateOf(appToEdit?.colorHex ?: "#1A73E8") }
    var notes by remember { mutableStateOf(appToEdit?.notes ?: "") }
    var desktopMode by remember { mutableStateOf(appToEdit?.desktopMode ?: false) }
    var textZoom by remember { mutableStateOf(appToEdit?.textZoom ?: 100) }

    var urlError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }

    val presetColors = listOf(
        "#1A73E8",
        "#0F9D58",
        "#F4B400",
        "#DB4437",
        "#E91E63",
        "#9C27B0",
        "#00BCD4",
        "#FF5722",
        "#E040FB"
    )

    val presetIcons = listOf(
        "language" to "Web Generale",
        "table_chart" to "Fogli / Tabelle",
        "calendar_today" to "Calendario",
        "analytics" to "Grafici / Statistiche",
        "terminal" to "Script / Admin",
        "feed" to "Form / Modulo",
        "forum" to "Chat / Messaggi",
        "dashboard" to "Pannello Hub",
        "storage" to "Database"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (appToEdit == null) "Aggiungi Web App" else "Modifica Web App",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_nav_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Informazioni di Collegamento",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            if (it.isNotEmpty()) nameError = null
                        },
                        label = { Text("Nome della Web App") },
                        placeholder = { Text("es. Gestione Ordini, Magazzino...") },
                        isError = nameError != null,
                        supportingText = {
                            if (nameError != null) {
                                Text(nameError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Un nome amichevole per riconoscere l'app.")
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_field")
                    )

                    OutlinedTextField(
                        value = url,
                        onValueChange = {
                            url = it
                            if (it.isNotEmpty()) urlError = null
                        },
                        label = { Text("URL di pubblicazione") },
                        placeholder = { Text("https://script.google.com/macros/s/.../exec") },
                        isError = urlError != null,
                        supportingText = {
                            if (urlError != null) {
                                Text(urlError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Inserisci l'URL del deployment che termina con /exec.")
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("url_field")
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Icona e Tema Colore",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text("Scegli un colore per l'accento", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        presetColors.forEach { colorString ->
                            val colorValue = Color(android.graphics.Color.parseColor(colorString))
                            val isSelected = selectedColor.equals(colorString, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                                    .clickable { selectedColor = colorString }
                                    .testTag("color_preset_$colorString"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selezionato",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Icona Rappresentativa", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            presetIcons.take(5).forEach { (iconKey, label) ->
                                InputChip(
                                    selected = selectedIcon == iconKey,
                                    onClick = { selectedIcon = iconKey },
                                    label = { Text(label.substringBefore(" "), fontSize = 11.sp) }, // Short labels
                                    leadingIcon = {
                                        Icon(
                                            imageVector = getIconByName(iconKey),
                                            contentDescription = label,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    modifier = Modifier.testTag("icon_chip_$iconKey")
                                )
                            }
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            presetIcons.drop(5).forEach { (iconKey, label) ->
                                InputChip(
                                    selected = selectedIcon == iconKey,
                                    onClick = { selectedIcon = iconKey },
                                    label = { Text(label.substringBefore(" "), fontSize = 11.sp) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = getIconByName(iconKey),
                                            contentDescription = label,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    modifier = Modifier.testTag("icon_chip_$iconKey")
                                )
                            }
                        }
                    }
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Impostazioni Visualizzazione",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Forza Modalità PC (Desktop)", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Carica l'interfaccia versione PC invece di quella mobile (utile per fogli incorporati e script larghi).",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = desktopMode,
                            onCheckedChange = { desktopMode = it },
                            modifier = Modifier.testTag("desktop_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Zoom Web: $textZoom%",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = textZoom.toFloat(),
                        onValueChange = { textZoom = it.toInt() },
                        valueRange = 50f..200f,
                        steps = 14,
                        modifier = Modifier.testTag("zoom_slider")
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Note personali / Commenti") },
                        placeholder = { Text("es. Scadenza token di accesso, autore...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = {
                    var hasError = false
                    if (name.trim().isEmpty()) {
                        nameError = "Inserisci un nome valido."
                        hasError = true
                    }
                    if (url.trim().isEmpty()) {
                        urlError = "L'URL è obbligatorio."
                        hasError = true
                    } else if (!url.startsWith("http://") && !url.startsWith("https://") && !url.contains(".")) {
                        urlError = "Inserisci un indirizzo valido."
                        hasError = true
                    }

                    if (!hasError) {
                        onSave(
                            name.trim(),
                            url.trim(),
                            selectedIcon,
                            selectedColor,
                            notes.trim(),
                            desktopMode,
                            textZoom
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_app_button")
            ) {
                Icon(Icons.Default.Save, contentDescription = "Salva")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Salva Applicazione", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
