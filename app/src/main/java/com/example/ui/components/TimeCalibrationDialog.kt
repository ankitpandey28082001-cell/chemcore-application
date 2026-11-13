package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.TimeUtils
import com.example.util.rememberCurrentIstDateTime
import com.example.util.rememberCurrentIstTimeWithSeconds
import kotlinx.coroutines.launch

@Composable
fun TimeCalibrationDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentOffset by TimeUtils.offsetFlow.collectAsState()
    val istTimeLiveWithSeconds by rememberCurrentIstTimeWithSeconds()
    val istDateTime by rememberCurrentIstDateTime()
    val syncStatusText by TimeUtils.syncStatusFlow.collectAsState()

    var isSyncing by remember { mutableStateOf(false) }
    var userMessage by remember { mutableStateOf<String?>(null) }

    val totalSeconds = Math.abs(currentOffset) / 1000L
    val offsetHours = totalSeconds / 3600L
    val offsetMinutes = (totalSeconds % 3600L) / 60L
    val offsetRemSec = totalSeconds % 60L
    val offsetSign = if (currentOffset >= 0) "+" else "-"

    val offsetDisplay = if (currentOffset == 0L) {
        "0s (Standard True IST)"
    } else {
        buildString {
            append(offsetSign)
            if (offsetHours > 0) append("${offsetHours}h ")
            if (offsetMinutes > 0 || offsetHours > 0) append("${offsetMinutes}m ")
            if (offsetRemSec > 0 || (offsetHours == 0L && offsetMinutes == 0L)) append("${offsetRemSec}s")
        }.trim()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "IST Time Calibration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Asia/Kolkata • UTC+05:30",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Live IST Clock Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "Indian Standard Time (Live)",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF047857),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = istTimeLiveWithSeconds,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF065F46)
                        )

                        Text(
                            text = istDateTime,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Offset Applied:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = offsetDisplay,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentOffset == 0L) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Auto Sync Button
                Button(
                    onClick = {
                        isSyncing = true
                        userMessage = "Syncing with atomic time server..."
                        coroutineScope.launch {
                            val res = TimeUtils.syncWithNetwork(context)
                            isSyncing = false
                            userMessage = if (res.isSuccess) {
                                "Synced successfully with live atomic time!"
                            } else {
                                "Network sync unreachable. Showing device IST time."
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sync_network_time_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Synchronizing with Atomic Clock...")
                    } else {
                        Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Auto-Sync with Atomic Time Server", fontWeight = FontWeight.Bold)
                    }
                }

                userMessage?.let { msg ->
                    Text(
                        text = msg,
                        fontSize = 11.sp,
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Text(
                    text = "Quick Calibration Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                // Presets: True IST (0) & +1h 30m / -1h 30m
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_ZERO, context)
                            userMessage = "Reset to standard IST (0s drift)"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_ist_zero_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = if (currentOffset == TimeUtils.OFFSET_ZERO) {
                            ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Standard IST (0)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_PLUS_1H_30M, context)
                            userMessage = "Applied +1h 30m offset"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_plus_1h30m_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = if (currentOffset == TimeUtils.OFFSET_PLUS_1H_30M) {
                            ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("+1h 30m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_MINUS_1H_30M, context)
                            userMessage = "Applied -1h 30m offset"
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("preset_minus_1h30m_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = if (currentOffset == TimeUtils.OFFSET_MINUS_1H_30M) {
                            ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        } else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("-1h 30m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Additional Presets (+30m, -30m, +1h, -1h)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_PLUS_30M, context)
                            userMessage = "Applied +30m offset"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+30m", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_MINUS_30M, context)
                            userMessage = "Applied -30m offset"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("-30m", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_PLUS_1H, context)
                            userMessage = "Applied +1h offset"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+1h", fontSize = 11.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            TimeUtils.setOffsetMillis(TimeUtils.OFFSET_MINUS_1H, context)
                            userMessage = "Applied -1h offset"
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("-1h", fontSize = 11.sp)
                    }
                }

                // Fine-tuning 1 minute buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fine-Tune by 1 Minute:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                TimeUtils.adjustOffset(-60_000L, context)
                                userMessage = "Adjusted -1 minute"
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("-1m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                TimeUtils.adjustOffset(60_000L, context)
                                userMessage = "Adjusted +1 minute"
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+1m", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Status: $syncStatusText",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Done")
            }
        }
    )
}
