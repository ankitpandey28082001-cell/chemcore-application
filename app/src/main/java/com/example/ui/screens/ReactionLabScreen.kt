package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reagent
import com.example.data.model.ReactionOutcome
import com.example.data.model.VirtualLabRepository
import com.example.ui.components.FlaskSimulationView

@Composable
fun ReactionLabScreen(
    modifier: Modifier = Modifier
) {
    val reagents = remember { VirtualLabRepository.availableReagents }

    var selectedReagent1 by remember { mutableStateOf<Reagent?>(reagents.first { it.id == "AgNO3" }) }
    var selectedReagent2 by remember { mutableStateOf<Reagent?>(reagents.first { it.id == "NaCl" }) }
    var isMixed by remember { mutableStateOf(true) }

    val reactionOutcome = remember(selectedReagent1, selectedReagent2, isMixed) {
        if (!isMixed || selectedReagent1 == null || selectedReagent2 == null) null
        else VirtualLabRepository.findReaction(selectedReagent1!!.id, selectedReagent2!!.id)
    }

    // Determine current visual state in flask
    val currentLiquidColor = when {
        !isMixed -> selectedReagent1?.initialColor ?: Color(0xFFE2E8F0)
        reactionOutcome != null -> reactionOutcome.finalLiquidColor
        selectedReagent1 != null && selectedReagent2 != null -> {
            // Mixed with no observable reaction
            Color(0xFFE2E8F0)
        }
        else -> Color(0xFFE2E8F0)
    }

    val hasBubbles = isMixed && (reactionOutcome?.hasGasBubbles == true)
    val hasPrecipitate = isMixed && (reactionOutcome?.hasPrecipitate == true)
    val precipitateColor = reactionOutcome?.precipitateColor ?: Color.Transparent

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bench Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Virtual Chemical Bench",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Combine laboratory reagents & observe live reactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = {
                    isMixed = false
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("reset_bench_btn")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Clean Flask")
            }
        }

        // Live Animated Beaker View
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .testTag("reaction_beaker_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                FlaskSimulationView(
                    liquidColor = currentLiquidColor,
                    hasGasBubbles = hasBubbles,
                    hasPrecipitate = hasPrecipitate,
                    precipitateColor = precipitateColor,
                    modifier = Modifier.fillMaxSize()
                )

                // Status Badge in top right
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = if (!isMixed) "Unmixed" else if (reactionOutcome != null) "Reaction Occurred!" else "Miscible Mixture",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (reactionOutcome != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Action Trigger Button
        Button(
            onClick = { isMixed = true },
            enabled = selectedReagent1 != null && selectedReagent2 != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("mix_reagents_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Science, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isMixed) "Reagents Mixed" else "Pour & Mix in Beaker",
                fontWeight = FontWeight.Bold
            )
        }

        // Reagent 1 Selector Shelf
        Text(
            "Reagent A (Cation / Acid / Salt)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reagents.forEach { reagent ->
                FilterChip(
                    selected = selectedReagent1?.id == reagent.id,
                    onClick = {
                        selectedReagent1 = reagent
                        isMixed = true
                    },
                    label = { Text(reagent.formula) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(reagent.initialColor)
                        )
                    },
                    modifier = Modifier.testTag("reagent1_${reagent.id}")
                )
            }
        }

        // Reagent 2 Selector Shelf
        Text(
            "Reagent B (Anion / Base / Metal)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            reagents.forEach { reagent ->
                FilterChip(
                    selected = selectedReagent2?.id == reagent.id,
                    onClick = {
                        selectedReagent2 = reagent
                        isMixed = true
                    },
                    label = { Text(reagent.formula) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(reagent.initialColor)
                        )
                    },
                    modifier = Modifier.testTag("reagent2_${reagent.id}")
                )
            }
        }

        // Reaction Analysis Card
        if (isMixed && reactionOutcome != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reaction_outcome_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Reaction Equation",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = reactionOutcome.balancedEquation,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    // Key Observations
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Thermodynamics", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(reactionOutcome.enthalpyType, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Observation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    if (reactionOutcome.hasPrecipitate) "Precipitate: ${reactionOutcome.precipitateName}"
                                    else if (reactionOutcome.hasGasBubbles) "Gas: ${reactionOutcome.gasName}"
                                    else "Color shift",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        "Scientific Explanation",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reactionOutcome.scientificExplanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Hazard Note
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reactionOutcome.hazardNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else if (isMixed) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No Chemical Reaction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "These two reagents form a homogeneous mixture without precipitation, gas generation, or significant thermal change.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
