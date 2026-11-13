package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.SpeechManager
import com.example.network.GeminiLiveService
import com.example.util.TimeUtils
import kotlinx.coroutines.launch

data class VoiceMessage(
    val id: String,
    val sender: String, // "user" or "gemini"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceConversationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val speechManager = remember { SpeechManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            speechManager.release()
        }
    }

    val isListening by speechManager.isListening.collectAsState()
    val isSpeaking by speechManager.isSpeaking.collectAsState()
    val speechRms by speechManager.speechRms.collectAsState()

    var isTtsEnabled by remember { mutableStateOf(true) }
    var textInput by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            VoiceMessage(
                id = "welcome",
                sender = "gemini",
                text = "Hello! I am your ChemMaster Live AI tutor, powered by gemini-3.8-live. Ask me anything about the periodic table, chemical reactions, formulas, or lab experiments!"
            )
        )
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = VoiceMessage(id = System.currentTimeMillis().toString(), sender = "user", text = userText)
        messages.add(userMsg)
        isThinking = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
            val history = messages.map { it.sender to it.text }
            val result = GeminiLiveService.chatWithLiveModel(history, userText)
            isThinking = false

            val replyText = result.getOrElse { err ->
                "I had trouble connecting to the live model: ${err.message}. Please check your network and API key."
            }

            val botMsg = VoiceMessage(id = (System.currentTimeMillis() + 1).toString(), sender = "gemini", text = replyText)
            messages.add(botMsg)
            listState.animateScrollToItem(messages.size - 1)

            if (isTtsEnabled) {
                speechManager.speak(replyText)
            }
        }
    }

    speechManager.onResultCallback = { recognized ->
        sendMessage(recognized)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechManager.startListening()
        }
    }

    val quickQuestions = remember {
        listOf(
            "Why is neon so unreactive?",
            "Explain how the Bohr model works",
            "What happens when HCl mixes with NaOH?",
            "Why does gold not oxidize like iron?",
            "How do valence electrons determine bonds?"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Live Model Status Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isListening || isSpeaking || isThinking) Color(0xFF10B981) else MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "gemini-3.8-live",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (isListening) "Listening to you..." else if (isThinking) "Thinking..." else if (isSpeaking) "Speaking response..." else "Live Voice Ready",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isTtsEnabled = !isTtsEnabled
                            if (!isTtsEnabled) speechManager.stopSpeaking()
                        },
                        modifier = Modifier.testTag("toggle_tts_btn")
                    ) {
                        Icon(
                            imageVector = if (isTtsEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Toggle Spoken Voice",
                            tint = if (isTtsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            messages.clear()
                            speechManager.stopSpeaking()
                        },
                        modifier = Modifier.testTag("clear_chat_btn")
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Chat")
                    }
                }
            }
        }

        // Animated Central Orb when speaking or listening
        AnimatedVisibility(
            visible = isListening || isSpeaking || isThinking,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                LiveVoiceWaveOrb(
                    isActive = isListening || isSpeaking || isThinking,
                    rms = speechRms,
                    isSpeaking = isSpeaking
                )
            }
        }

        // Conversation Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("voice_chat_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUser) "You" else "gemini-3.8-live",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f) else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = TimeUtils.formatIstTime(msg.timestamp),
                                    fontSize = 10.sp,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyzing chemistry...", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { prompt ->
                AssistChip(
                    onClick = { sendMessage(prompt) },
                    label = { Text(prompt, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    modifier = Modifier.testTag("quick_question_chip")
                )
            }
        }

        // Voice Microphone / Text Input Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("voice_chat_text_input"),
                placeholder = { Text("Ask voice assistant or type...") },
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    if (textInput.isNotEmpty()) {
                        IconButton(onClick = {
                            val msg = textInput
                            textInput = ""
                            sendMessage(msg)
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                singleLine = true
            )

            // Large Microphone FAB
            val micContainerColor = if (isListening) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary
            FilledIconButton(
                onClick = {
                    if (isListening) {
                        speechManager.stopListening()
                    } else {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED

                        if (hasPermission) {
                            speechManager.startListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                modifier = Modifier
                    .size(54.dp)
                    .testTag("mic_toggle_button"),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = micContainerColor)
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Speak to Live Model",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun LiveVoiceWaveOrb(
    isActive: Boolean,
    rms: Float,
    isSpeaking: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val coreColor = if (isSpeaking) Color(0xFF38BDF8) else Color(0xFF10B981)

    Canvas(modifier = Modifier.size(80.dp)) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val baseRadius = 24f * pulseScale * (1f + (rms.coerceAtLeast(0f) / 10f))

        // Outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(coreColor.copy(alpha = 0.6f), coreColor.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(centerX, centerY),
                radius = baseRadius * 1.8f
            ),
            radius = baseRadius * 1.8f,
            center = Offset(centerX, centerY)
        )

        // Core orb
        drawCircle(
            color = coreColor,
            radius = baseRadius,
            center = Offset(centerX, centerY)
        )
    }
}
