package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReferenceRepository

@Composable
fun ReferenceScreen(
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 60.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedSection,
            modifier = Modifier.fillMaxWidth(),
            edgePadding = 0.dp
        ) {
            Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }, text = { Text("Solubility Rules") })
            Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }, text = { Text("Polyatomic Ions") })
            Tab(selected = selectedSection == 2, onClick = { selectedSection = 2 }, text = { Text("Activity Series") })
            Tab(selected = selectedSection == 3, onClick = { selectedSection = 3 }, text = { Text("Constants") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSection) {
            0 -> SolubilityRulesTab()
            1 -> PolyatomicIonsTab()
            2 -> ActivitySeriesTab()
            3 -> ConstantsTab()
        }
    }
}

@Composable
fun SolubilityRulesTab() {
    val rules = remember { ReferenceRepository.solubilityRules }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("solubility_rules_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(rules) { rule ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = rule.category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(
                            containerColor = if (rule.isGenerallySoluble) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                            contentColor = if (rule.isGenerallySoluble) Color(0xFF10B981) else Color(0xFFEF4444)
                        ) {
                            Text(if (rule.isGenerallySoluble) "SOLUBLE" else "INSOLUBLE", fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp))
                        }
                    }
                    Text(text = rule.rule, style = MaterialTheme.typography.bodyMedium)
                    if (rule.exceptions.isNotEmpty()) {
                        Text(
                            text = "Exceptions: ${rule.exceptions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolyatomicIonsTab() {
    val ions = remember { ReferenceRepository.polyatomicIons }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("polyatomic_ions_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ions) { ion ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = ion.formula,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = ion.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Charge: ${ion.charge}", fontSize = 10.sp) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        Text(
                            text = "Examples: ${ion.commonExamples}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivitySeriesTab() {
    val series = remember { ReferenceRepository.activitySeries }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("activity_series_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ranked from Most Reactive (top) to Least Reactive (bottom)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        items(series) { (metal, description) ->
            val isHydrogen = metal.contains("HYDROGEN")
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isHydrogen) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = metal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun ConstantsTab() {
    val constants = remember { ReferenceRepository.constants }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("constants_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(constants) { c ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = c.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = c.symbol,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${c.value}  ${c.unit}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = c.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
