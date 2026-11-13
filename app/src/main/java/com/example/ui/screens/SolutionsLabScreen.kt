package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.SolutionsCalculator
import kotlin.math.roundToInt

@Composable
fun SolutionsLabScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: pH Lab, 1: Dilution Calculator

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 60.dp)
    ) {
        // Tab switcher
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            SegmentedButton(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("pH & Acidity Lab")
            }
            SegmentedButton(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("Dilution (M₁V₁ = M₂V₂)")
            }
        }

        if (selectedTab == 0) {
            PhLabContent()
        } else {
            DilutionContent()
        }
    }
}

@Composable
fun PhLabContent() {
    var phSliderValue by remember { mutableFloatStateOf(7.0f) }
    val phResult = remember(phSliderValue) {
        SolutionsCalculator.calculateFromPh(phSliderValue.toDouble())
    }

    val animatedColor by animateColorAsState(
        targetValue = phResult.indicatorColor,
        label = "ph_indicator_color"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Visual Indicator Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = animatedColor.copy(alpha = 0.20f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "pH ${String.format("%.1f", phResult.ph)}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = animatedColor
                )
                Text(
                    text = phResult.acidityType,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Comparable to: ${phResult.commonReference}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // pH Spectrum Strip
        Text(
            "pH Universal Indicator Spectrum",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFEF4444), // pH 0
                            Color(0xFFF97316), // pH 3
                            Color(0xFFFBBF24), // pH 5
                            Color(0xFF22C55E), // pH 7
                            Color(0xFF06B6D4), // pH 9
                            Color(0xFF6366F1), // pH 12
                            Color(0xFF8B5CF6)  // pH 14
                        )
                    )
                )
        )

        // Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0 (Extreme Acid)", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                Text("7 (Neutral)", fontSize = 11.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                Text("14 (Extreme Base)", fontSize = 11.sp, color = Color(0xFF8B5CF6), fontWeight = FontWeight.Bold)
            }
            Slider(
                value = phSliderValue,
                onValueChange = { phSliderValue = (it * 10f).roundToInt() / 10f },
                valueRange = 0f..14f,
                steps = 139,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ph_slider")
            )
        }

        // Quick Preset Reference Chips
        Text(
            "Benchmark Substances",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SolutionsCalculator.commonSubstances.forEach { sub ->
                FilterChip(
                    selected = Math.abs(phResult.ph - sub.ph) < 0.2,
                    onClick = { phSliderValue = sub.ph.toFloat() },
                    label = { Text("${sub.name} (${sub.ph})") },
                    modifier = Modifier.testTag("ph_chip_${sub.ph}")
                )
            }
        }

        // Detailed Ion Concentrations
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Solution Chemistry Equilibrium",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyStatCard("pOH Value", String.format("%.2f", phResult.poh), Modifier.weight(1f))
                    PropertyStatCard("Product [H⁺][OH⁻]", "1.0 × 10⁻¹⁴ (Kw)", Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PropertyStatCard("[H⁺] Hydronium", String.format("%.2e M", phResult.hConcentration), Modifier.weight(1f))
                    PropertyStatCard("[OH⁻] Hydroxide", String.format("%.2e M", phResult.ohConcentration), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DilutionContent() {
    var m1Input by remember { mutableStateOf("12.0") } // Initial Molarity
    var v1Input by remember { mutableStateOf("") }     // Initial Volume (mL)
    var m2Input by remember { mutableStateOf("1.0") }  // Target Molarity
    var v2Input by remember { mutableStateOf("500.0") } // Target Volume (mL)

    val m1 = m1Input.toDoubleOrNull()
    val v1 = v1Input.toDoubleOrNull()
    val m2 = m2Input.toDoubleOrNull()
    val v2 = v2Input.toDoubleOrNull()

    val dilutionResult = remember(m1, v1, m2, v2) {
        SolutionsCalculator.solveDilution(m1, v1, m2, v2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Solution Dilution Formula",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "M₁ × V₁ = M₂ × V₂",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Enter any 3 values to solve for the missing 4th variable.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Input 2x2 Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = m1Input,
                onValueChange = { m1Input = it },
                label = { Text("M₁ (Stock Conc. M)") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dilution_m1_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = v1Input,
                onValueChange = { v1Input = it },
                label = { Text("V₁ (Stock Vol. mL)") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dilution_v1_input"),
                placeholder = { Text("Auto-solves") },
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = m2Input,
                onValueChange = { m2Input = it },
                label = { Text("M₂ (Target Conc. M)") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dilution_m2_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = v2Input,
                onValueChange = { v2Input = it },
                label = { Text("V₂ (Target Vol. mL)") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("dilution_v2_input"),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
        }

        // Dilution Result Card
        dilutionResult?.let { res ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dilution_result_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Dilution Recipe",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "1. Measure ${String.format("%.2f", res.v1)} mL of ${String.format("%.2f", res.m1)} M stock solution.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "2. Add ${String.format("%.2f", res.waterToAdd)} mL of distilled solvent (water) until the total volume reaches ${String.format("%.2f", res.v2)} mL.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Result: ${String.format("%.2f", res.v2)} mL of ${String.format("%.2f", res.m2)} M solution.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } ?: run {
            Text(
                "Leave one field empty (e.g. V₁) to compute its required volume or concentration.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
