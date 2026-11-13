package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.MolarMassCalculator
import com.example.calculator.MolarMassResult
import com.example.ui.components.ChemKeypadBar

@Composable
fun MolarMassScreen(
    modifier: Modifier = Modifier
) {
    var formulaInput by remember { mutableStateOf("CuSO4*5H2O") }
    var massResult by remember { mutableStateOf<MolarMassResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Stoichiometry converter states
    var inputGrams by remember { mutableStateOf("100.0") }
    var inputMoles by remember { mutableStateOf("1.0") }

    val presetFormulas = remember {
        listOf(
            "H2O" to "Water",
            "C6H12O6" to "Glucose",
            "CaCO3" to "Calcium Carbonate",
            "CuSO4*5H2O" to "Blue Vitriol",
            "H2SO4" to "Sulfuric Acid",
            "NaCl" to "Sodium Chloride",
            "KMnO4" to "Potassium Permanganate",
            "Fe2(SO4)3" to "Iron(III) Sulfate"
        )
    }

    LaunchedEffect(formulaInput) {
        if (formulaInput.isNotBlank()) {
            val res = MolarMassCalculator.calculateDetailed(formulaInput)
            if (res.isSuccess) {
                massResult = res.getOrNull()
                errorMessage = null
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
        // Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "Molar Mass & Stoichiometry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Elemental mass breakdown, moles & molecule counts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Preset Chips
        Text(
            "Common Compounds",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetFormulas.forEach { (presetF, label) ->
                AssistChip(
                    onClick = {
                        formulaInput = presetF
                        val res = MolarMassCalculator.calculateDetailed(presetF)
                        if (res.isSuccess) {
                            massResult = res.getOrNull()
                            errorMessage = null
                        } else {
                            errorMessage = res.exceptionOrNull()?.message
                        }
                    },
                    label = { Text("$presetF ($label)") },
                    modifier = Modifier.testTag("preset_formula_$presetF")
                )
            }
        }

        // Input Field
        OutlinedTextField(
            value = formulaInput,
            onValueChange = {
                formulaInput = it
                val res = MolarMassCalculator.calculateDetailed(it)
                if (res.isSuccess) {
                    massResult = res.getOrNull()
                    errorMessage = null
                } else {
                    errorMessage = res.exceptionOrNull()?.message
                }
            },
            label = { Text("Chemical Formula") },
            placeholder = { Text("e.g. Ca(OH)2 or CuSO4*5H2O") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("molar_mass_input"),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                if (formulaInput.isNotEmpty()) {
                    IconButton(onClick = { formulaInput = ""; massResult = null }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Keypad helper
        ChemKeypadBar(
            onInsertText = { token ->
                val converted = when (token) {
                    "₂" -> "2"; "₃" -> "3"; "₄" -> "4"; "₅" -> "5"; "₆" -> "6"
                    else -> token
                }
                formulaInput += converted
            }
        )

        // Error message if any
        AnimatedVisibility(visible = errorMessage != null && formulaInput.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Result Card
        massResult?.let { result ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("molar_mass_result_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "Total Molar Mass",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = String.format("%.3f", result.totalMolarMass),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "g / mol",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    HorizontalDivider()

                    Text(
                        "Elemental Mass Composition",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Percentage Breakdown Bars
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        result.contributions.forEach { contrib ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(contrib.element.category.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${contrib.element.name} (${contrib.element.symbol} × ${contrib.count})",
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    Text(
                                        text = "${String.format("%.1f", contrib.percentage)}%",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = contrib.element.category.color
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (contrib.percentage / 100.0).toFloat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = contrib.element.category.color,
                                    trackColor = MaterialTheme.colorScheme.surface
                                )
                                Text(
                                    text = "${String.format("%.3f", contrib.totalWeight)} g/mol",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Stoichiometry Converter
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Stoichiometry Converter",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val massGrams = inputGrams.toDoubleOrNull() ?: 0.0
                    val calculatedMoles = MolarMassCalculator.gramsToMoles(massGrams, result.totalMolarMass)
                    val molecules = MolarMassCalculator.molesToMolecules(calculatedMoles)

                    OutlinedTextField(
                        value = inputGrams,
                        onValueChange = { inputGrams = it },
                        label = { Text("Mass in Grams (g)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Moles (mol)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(String.format("%.4f", calculatedMoles), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Molecules (N)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(String.format("%.2e", molecules), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
