package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ElementCategory(val displayName: String, val color: Color) {
    ALKALI_METAL("Alkali Metal", CatAlkaliMetal),
    ALKALINE_EARTH("Alkaline Earth Metal", CatAlkalineEarth),
    TRANSITION_METAL("Transition Metal", CatTransitionMetal),
    POST_TRANSITION("Post-Transition Metal", CatPostTransition),
    METALLOID("Metalloid", CatMetalloid),
    REACTIVE_NONMETAL("Reactive Nonmetal", CatNonmetal),
    HALOGEN("Halogen", CatHalogen),
    NOBLE_GAS("Noble Gas", CatNobleGas),
    LANTHANIDE("Lanthanide", CatLanthanide),
    ACTINIDE("Actinide", CatActinide),
    UNKNOWN("Unknown Properties", CatUnknown)
}

data class Element(
    val atomicNumber: Int,
    val symbol: String,
    val name: String,
    val atomicWeight: Double,
    val category: ElementCategory,
    val period: Int,
    val group: Int,
    val block: String,
    val electronConfiguration: String,
    val electronegativity: Double? = null,
    val meltingPointKelvin: Double? = null,
    val boilingPointKelvin: Double? = null,
    val density: Double? = null, // g/cm³ or g/L for gases
    val phaseAtSTP: String, // Solid, Liquid, Gas
    val discoveryYear: String,
    val discoveredBy: String,
    val summary: String,
    val shells: List<Int>,
    val oxidationStates: List<Int> = emptyList()
)
