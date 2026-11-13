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
import com.example.calculator.BalancedReactionResult
import com.example.calculator.ChemicalEquationBalancer
import com.example.ui.components.ChemKeypadBar

@Composable
fun EquationBalancerScreen(
    initialEquation: String = "",
    modifier: Modifier = Modifier
) {
    var equationInput by remember { mutableStateOf(if (initialEquation.isNotBlank()) "$initialEquation + O2 -> " else "C3H8 + O2 -> CO2 + H2O") }
    var balanceResult by remember { mutableStateOf<BalancedReactionResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val presetReactions = remember {
        listOf(
            "C3H8 + O2 -> CO2 + H2O" to "Propane Combustion",
            "Fe + O2 -> Fe2O3" to "Rusting / Iron Oxidation",
            "KMnO4 + HCl -> KCl + MnCl2 + Cl2 + H2O" to "Redox Acid Reaction",
            "Al + CuSO4 -> Al2(SO4)3 + Cu" to "Single Displacement",
            "NaOH + H2SO4 -> Na2SO4 + H2O" to "Acid-Base Neutralization",
            "H2 + O2 -> H2O" to "Hydrogen Combustion",
            "AgNO3 + NaCl -> AgCl + NaNO3" to "Precipitation"
        )
    }

    LaunchedEffect(Unit) {
        if (equationInput.contains("->")) {
            val res = ChemicalEquationBalancer.balance(equationInput)
            if (res.isSuccess) {
                balanceResult = res.getOrNull()
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
        // Hero Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
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
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Balance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        "Chemical Equation Balancer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Computes stoichiometric coefficients & atom conservation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Preset Chips
        Text(
            "Try Classic Reactions",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetReactions.forEach { (presetEq, label) ->
                AssistChip(
                    onClick = {
                        equationInput = presetEq
                        val res = ChemicalEquationBalancer.balance(presetEq)
                        if (res.isSuccess) {
                            balanceResult = res.getOrNull()
                            errorMessage = null
                        } else {
                            balanceResult = null
                            errorMessage = res.exceptionOrNull()?.message
                        }
                    },
                    label = { Text(label) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("preset_reaction_$label")
                )
            }
        }

        // Input Field
        OutlinedTextField(
            value = equationInput,
            onValueChange = {
                equationInput = it
                errorMessage = null
            },
            label = { Text("Chemical Equation") },
            placeholder = { Text("e.g. C3H8 + O2 -> CO2 + H2O") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("equation_input_field"),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                if (equationInput.isNotEmpty()) {
                    IconButton(onClick = { equationInput = ""; balanceResult = null }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Quick Chemical Keypad helper
        ChemKeypadBar(
            onInsertText = { token ->
                val converted = when (token) {
                    "₂" -> "2"; "₃" -> "3"; "₄" -> "4"; "₅" -> "5"; "₆" -> "6"
                    else -> token
                }
                equationInput += if (converted == "+" || converted == "->") " $converted " else converted
            }
        )

        // Balance Action Button
        Button(
            onClick = {
                val res = ChemicalEquationBalancer.balance(equationInput)
                if (res.isSuccess) {
                    balanceResult = res.getOrNull()
                    errorMessage = null
                } else {
                    balanceResult = null
                    errorMessage = res.exceptionOrNull()?.message ?: "Failed to balance equation"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("balance_equation_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Balance Equation", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Error message if any
        AnimatedVisibility(visible = errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Balance Result
        balanceResult?.let { result ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_result_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Balanced Equation",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text(result.reactionType, fontSize = 11.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                labelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Equation display banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = result.balancedEquation,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Atom Conservation Table
                    Text(
                        "Atom Conservation Check",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        result.elementConservation.forEach { (elem, cons) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = elem,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "Reactants: ${cons.reactantCount}  =  Products: ${cons.productCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Species Molar Masses
                    Text(
                        "Participant Molar Masses",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        (result.reactants + result.products).distinctBy { it.formula }.forEach { comp ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = comp.formula,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${String.format("%.2f", comp.molarMass)} g/mol",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
