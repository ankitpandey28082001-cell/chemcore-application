package com.example.calculator

import androidx.compose.ui.graphics.Color
import kotlin.math.log10
import kotlin.math.pow

data class PhCalculationResult(
    val ph: Double,
    val poh: Double,
    val hConcentration: Double,  // mol/L
    val ohConcentration: Double, // mol/L
    val acidityType: String,
    val indicatorColor: Color,
    val commonReference: String
)

data class SubstanceReference(
    val name: String,
    val ph: Double,
    val description: String
)

object SolutionsCalculator {

    val commonSubstances = listOf(
        SubstanceReference("Battery Acid (1M H₂SO₄)", 0.0, "Extremely strong mineral acid"),
        SubstanceReference("Stomach Acid (HCl)", 1.5, "Aids human digestion, activates pepsin"),
        SubstanceReference("Lemon Juice / Vinegar", 2.4, "Citric & acetic acids, pungent taste"),
        SubstanceReference("Cola / Soda", 3.0, "Phosphoric acid, carbonic acid"),
        SubstanceReference("Black Coffee", 5.0, "Mild organic chlorogenic acids"),
        SubstanceReference("Pure Cow's Milk", 6.6, "Nearly neutral, slightly acidic lactic acid"),
        SubstanceReference("Pure Water (25°C)", 7.0, "Neutral: [H⁺] equals [OH⁻]"),
        SubstanceReference("Human Blood", 7.40, "Carefully buffered physiological pH"),
        SubstanceReference("Seawater", 8.2, "Mild alkaline carbonate buffer"),
        SubstanceReference("Baking Soda (NaHCO₃)", 9.0, "Mild amphiprotic sodium bicarbonate base"),
        SubstanceReference("Milk of Magnesia (Mg(OH)₂)", 10.5, "Antacid suspension"),
        SubstanceReference("Household Ammonia", 11.5, "Pungent aqueous NH₃ cleaning agent"),
        SubstanceReference("Bleach (NaClO)", 12.5, "Strong oxidizing household disinfectant"),
        SubstanceReference("Drain Cleaner (1M NaOH)", 14.0, "Extremely caustic lye alkali")
    )

    fun calculateFromPh(phValue: Double): PhCalculationResult {
        val clampedPh = phValue.coerceIn(0.0, 14.0)
        val poh = 14.0 - clampedPh
        val hConc = 10.0.pow(-clampedPh)
        val ohConc = 10.0.pow(-poh)

        val acidity = when {
            clampedPh < 6.8 -> "Acidic (Excess H⁺ / H₃O⁺)"
            clampedPh > 7.2 -> "Basic / Alkaline (Excess OH⁻)"
            else -> "Neutral (Balanced H⁺ & OH⁻)"
        }

        val color = getPhColor(clampedPh)
        val closestRef = commonSubstances.minByOrNull { Math.abs(it.ph - clampedPh) }?.name ?: ""

        return PhCalculationResult(
            ph = clampedPh,
            poh = poh,
            hConcentration = hConc,
            ohConcentration = ohConc,
            acidityType = acidity,
            indicatorColor = color,
            commonReference = closestRef
        )
    }

    fun calculateFromHConc(hConc: Double): PhCalculationResult {
        val safeConc = if (hConc <= 0) 1e-14 else hConc
        val ph = (-log10(safeConc)).coerceIn(0.0, 14.0)
        return calculateFromPh(ph)
    }

    fun getPhColor(ph: Double): Color {
        return when {
            ph < 2.0 -> Color(0xFFEF4444) // Intense Red
            ph < 4.0 -> Color(0xFFF97316) // Orange
            ph < 6.0 -> Color(0xFFFBBF24) // Yellow
            ph < 6.8 -> Color(0xFFA3E635) // Yellow-Green
            ph in 6.8..7.2 -> Color(0xFF22C55E) // Pure Green (Neutral)
            ph < 9.0 -> Color(0xFF14B8A6) // Teal
            ph < 11.0 -> Color(0xFF06B6D4) // Cyan / Blue
            ph < 12.5 -> Color(0xFF6366F1) // Indigo
            else -> Color(0xFF8B5CF6)     // Deep Purple
        }
    }

    /**
     * Dilution: M1 * V1 = M2 * V2
     * Solve for unknown among (m1, v1, m2, v2)
     */
    fun solveDilution(
        m1: Double?,
        v1: Double?,
        m2: Double?,
        v2: Double?
    ): DilutionResult? {
        // Count how many are provided
        val nonNull = listOf(m1, v1, m2, v2).count { it != null && it > 0 }
        if (nonNull < 3) return null

        var solvedM1 = m1
        var solvedV1 = v1
        var solvedM2 = m2
        var solvedV2 = v2

        when {
            m1 == null || m1 <= 0 -> {
                if (v1 != null && m2 != null && v2 != null && v1 > 0) {
                    solvedM1 = (m2 * v2) / v1
                }
            }
            v1 == null || v1 <= 0 -> {
                if (m1 != null && m2 != null && v2 != null) {
                    solvedV1 = (m2 * v2) / m1
                }
            }
            m2 == null || m2 <= 0 -> {
                if (m1 != null && v1 != null && v2 != null) {
                    solvedM2 = (m1 * v1) / v2
                }
            }
            v2 == null || v2 <= 0 -> {
                if (m1 != null && v1 != null && m2 != null) {
                    solvedV2 = (m1 * v1) / m2
                }
            }
        }

        if (solvedM1 == null || solvedV1 == null || solvedM2 == null || solvedV2 == null) return null

        val waterToAdd = (solvedV2 - solvedV1).coerceAtLeast(0.0)

        return DilutionResult(
            m1 = solvedM1,
            v1 = solvedV1,
            m2 = solvedM2,
            v2 = solvedV2,
            waterToAdd = waterToAdd
        )
    }
}

data class DilutionResult(
    val m1: Double,
    val v1: Double,
    val m2: Double,
    val v2: Double,
    val waterToAdd: Double
)
