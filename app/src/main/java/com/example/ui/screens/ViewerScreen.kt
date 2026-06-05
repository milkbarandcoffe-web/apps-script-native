package com.example.ui.screens

import android.webkit.WebView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WebApp
import com.example.ui.components.WebAppWebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerScreen(
    app: WebApp,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isExpandedMenu by remember { mutableStateOf(false) }

    var desktopModeState by remember { mutableStateOf(app.desktopMode) }
    var textZoomState by remember { mutableStateOf(app.textZoom) }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    val context = LocalContext.current

    val appThemeColor = remember(app.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(app.colorHex))
        } catch (e: Exception) {
            Color(0xFF03A9F4)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = app.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        Text(
                            text = if (isLoading) "Caricamento in corso ($progress%)..." else app.url,
                            fontSize = 11.sp,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("viewer_close_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi ed esci")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { webViewInstance?.reload() },
                        modifier = Modifier.testTag("viewer_refresh_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Ricarica")
                    }
                    IconButton(
                        onClick = { isExpandedMenu = !isExpandedMenu },
                        modifier = Modifier.testTag("viewer_menu_button")
                    ) {
                        Icon(
                            imageVector = if (isExpandedMenu) Icons.Default.SettingsApplications else Icons.Default.Tune,
                            contentDescription = "Configurazioni"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    color = appThemeColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (errorMsg != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(MaterialTheme.colorScheme.errorContainer, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Errore connessione",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Errore di caricamento",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = errorMsg ?: "Impossibile caricare l'interfaccia dell'applicazione Google Apps Script. Controlla il collegamento internet o l'URL.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                errorMsg = null
                                isLoading = true
                                webViewInstance?.loadUrl(app.url)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = appThemeColor)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Riprova")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Riprova a caricare")
                        }
                    }
                }

                WebAppWebView(
                    url = app.url,
                    desktopMode = desktopModeState,
                    textZoom = textZoomState,
                    onProgressChanged = {
                        progress = it
                        if (it >= 100) {
                            isLoading = false
                        }
                    },
                    onPageStarted = {
                        isLoading = true
                        errorMsg = null
                    },
                    onPageFinished = {
                        isLoading = false
                    },
                    onPageError = {
                        errorMsg = it
                        isLoading = false
                    },
                    onWebViewCreated = { webView ->
                        webViewInstance = webView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { webViewInstance?.goBack() },
                    enabled = webViewInstance?.canGoBack() == true,
                    modifier = Modifier.testTag("webview_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Indietro",
                        tint = if (webViewInstance?.canGoBack() == true) appThemeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                IconButton(
                    onClick = { webViewInstance?.goForward() },
                    enabled = webViewInstance?.canGoForward() == true,
                    modifier = Modifier.testTag("webview_forward")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Avanti",
                        tint = if (webViewInstance?.canGoForward() == true) appThemeColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                IconButton(
                    onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, webViewInstance?.url ?: app.url)
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Condividi link web app"))
                    },
                    modifier = Modifier.testTag("webview_share")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Condividi"
                    )
                }

                IconButton(
                    onClick = {
                        android.webkit.CookieManager.getInstance().removeAllCookies {
                            webViewInstance?.reload()
                        }
                    },
                    modifier = Modifier.testTag("webview_clear_cookies")
                ) {
                    Icon(
                        imageVector = Icons.Default.NoAccounts,
                        contentDescription = "Scollega Google",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }

        if (isExpandedMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { isExpandedMenu = false }
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable(enabled = false) {},
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Opzioni Rapide",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { isExpandedMenu = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Chiudi")
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Forza versione Desktop (PC)", fontWeight = FontWeight.SemiBold)
                                Text("Abilita per caricare la versione desktop completa", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = desktopModeState,
                                onCheckedChange = { desktopModeState = it }
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Zoom Testo: $textZoomState%", fontWeight = FontWeight.SemiBold)
                            Slider(
                                value = textZoomState.toFloat(),
                                onValueChange = { textZoomState = it.toInt() },
                                valueRange = 50f..200f,
                                steps = 14
                            )
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            text = "Se riscontri problemi di permessi o login, tocca l'icona rossa in basso a destra per scollegare la sessione Google corrente e ripetere il login.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Button(
                            onClick = { isExpandedMenu = false },
                            colors = ButtonDefaults.buttonColors(containerColor = appThemeColor),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Applica Impostazioni", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
