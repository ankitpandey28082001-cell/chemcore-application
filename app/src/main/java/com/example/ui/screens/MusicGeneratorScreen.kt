package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MusicPlayerManager
import com.example.network.GeminiLiveService
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicGeneratorScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val playerManager = remember { MusicPlayerManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            playerManager.release()
        }
    }

    val isPlaying by playerManager.isPlaying.collectAsState()
    val currentPosition by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    val trackTitle by playerManager.trackTitle.collectAsState()

    var isShortClip by remember { mutableStateOf(true) } // true: lyria-3-clip-preview, false: lyria-3-pro-preview
    var musicPrompt by remember { mutableStateOf("Lo-fi chill beats for balancing chemical equations with warm vinyl crackle and mellow electric piano") }
    var isGenerating by remember { mutableStateOf(false) }
    var currentAudioFile by remember { mutableStateOf<File?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val presetVibes = remember {
        listOf(
            "Lo-fi Equation Beats" to "Lo-fi chill beats for balancing chemical equations with warm vinyl crackle and mellow electric piano",
            "Ambient Orbital Drone" to "Ethereal ambient synthesizer resonance, gentle meditation drone representing atomic electron shells",
            "Synthwave Electro Lab" to "Retro 80s synthwave with pulsing basslines and sparkling arpeggios for late-night chemistry study",
            "Classical Focus Piano" to "Calm, reflective neoclassical solo piano for deep focus and memorizing the periodic table",
            "Energetic Study Groove" to "Upbeat downtempo instrumental beats with acoustic guitar and light percussion"
        )
    }

    fun triggerGeneration() {
        if (musicPrompt.isBlank()) return
        isGenerating = true
        statusMessage = "Composing music using ${if (isShortClip) "lyria-3-clip-preview" else "lyria-3-pro-preview"}..."

        coroutineScope.launch {
            val result = GeminiLiveService.generateMusic(
                prompt = musicPrompt,
                isShortClip = isShortClip,
                cacheDir = context.cacheDir
            )
            isGenerating = false

            if (result.isSuccess) {
                val file = result.getOrThrow()
                currentAudioFile = file
                val modelTag = if (isShortClip) "Lyria Clip (30s)" else "Lyria Pro Full Track"
                statusMessage = "Track generated successfully!"
                playerManager.playFile(file, "$modelTag: ${musicPrompt.take(30)}...")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Unknown error"
                statusMessage = "Note: Lyria generation response: $err. Playing synthesized 432Hz scientific focus preview."
                // Synthesize relaxing study tone as offline/emulator preview
                val fallbackFile = playerManager.playSynthesizedStudyTone("432Hz Quantum Harmonic Focus (Preview)")
                currentAudioFile = fallbackFile
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "Chemistry Study Music Studio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "AI Music Generation powered by Lyria",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Model Selector Segmented Control
        Text(
            "Select Lyria Model Mode",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = isShortClip,
                onClick = { isShortClip = true },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                modifier = Modifier.testTag("model_lyria_clip_btn")
            ) {
                Text("Short Clip (≤30s)\nlyria-3-clip-preview", textAlign = TextAlign.Center, fontSize = 11.sp)
            }
            SegmentedButton(
                selected = !isShortClip,
                onClick = { isShortClip = false },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                modifier = Modifier.testTag("model_lyria_pro_btn")
            ) {
                Text("Full-Length Track\nlyria-3-pro-preview", textAlign = TextAlign.Center, fontSize = 11.sp)
            }
        }

        // Preset Music Themes
        Text(
            "Study Vibes Presets",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetVibes.forEach { (name, prompt) ->
                AssistChip(
                    onClick = { musicPrompt = prompt },
                    label = { Text(name) },
                    leadingIcon = {
                        Icon(Icons.Default.Headphones, contentDescription = null, modifier = Modifier.size(14.dp))
                    },
                    modifier = Modifier.testTag("preset_vibe_$name")
                )
            }
        }

        // Custom Music Prompt Input
        OutlinedTextField(
            value = musicPrompt,
            onValueChange = { musicPrompt = it },
            label = { Text("Music Composition Prompt") },
            placeholder = { Text("Describe instruments, mood, bpm, tempo...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("music_prompt_input"),
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            maxLines = 4
        )

        // Generate Action Button
        Button(
            onClick = { triggerGeneration() },
            enabled = !isGenerating && musicPrompt.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_music_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Generating with ${if (isShortClip) "lyria-3-clip-preview" else "lyria-3-pro-preview"}...")
            } else {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Generate Music (${if (isShortClip) "30s Clip" else "Full Track"})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        // Status or Error info
        statusMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(msg, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Music Player Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("music_player_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Equalizer Visualizer
                SoundEqualizerVisualizer(
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                // Track Title & Active Model Badge
                Text(
                    text = trackTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = if (isShortClip) "Model: lyria-3-clip-preview" else "Model: lyria-3-pro-preview",
                            fontSize = 11.sp
                        )
                    }
                )

                // Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    val progressFloat = if (duration > 0) (currentPosition.toFloat() / duration).coerceIn(0f, 1f) else 0f
                    Slider(
                        value = progressFloat,
                        onValueChange = { frac ->
                            if (duration > 0) {
                                playerManager.seekTo((frac * duration).toInt())
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(formatMs(currentPosition), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatMs(duration), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Playback Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = {
                            playerManager.playSynthesizedStudyTone("432Hz Quantum Harmonic Focus")
                        },
                        modifier = Modifier.testTag("preview_synth_tone_btn")
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = "Play Synth Focus Preview")
                    }

                    FilledIconButton(
                        onClick = { playerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("play_pause_music_btn"),
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            playerManager.seekTo(0)
                        },
                        modifier = Modifier.testTag("replay_music_btn")
                    ) {
                        Icon(Icons.Default.Replay, contentDescription = "Replay")
                    }
                }
            }
        }
    }
}

@Composable
fun SoundEqualizerVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eq_wave"
    )

    val barColor = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val count = 28
        val spacing = size.width / count
        val barWidth = spacing * 0.6f

        for (i in 0 until count) {
            val factor = if (isPlaying) {
                val phase = (i.toFloat() / count) * Math.PI.toFloat() * 2f + (animOffset * Math.PI.toFloat())
                (Math.sin(phase.toDouble()).toFloat() * 0.4f + 0.5f).coerceIn(0.15f, 0.95f)
            } else {
                0.15f
            }

            val barHeight = size.height * factor
            val x = i * spacing + (spacing - barWidth) / 2f
            val y = size.height - barHeight

            drawRoundRect(
                color = barColor.copy(alpha = if (isPlaying) 0.85f else 0.35f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

private fun formatMs(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
