package com.example.calculator

import com.example.data.model.Element
import com.example.data.model.PeriodicTableRepository

data class ElementContribution(
    val element: Element,
    val count: Int,
    val totalWeight: Double,
    val percentage: Double
)

data class MolarMassResult(
    val formula: String,
    val totalMolarMass: Double,
    val contributions: List<ElementContribution>
)

object MolarMassCalculator {

    const val AVOGADRO_CONSTANT = 6.02214076e23

    fun calculateMolarMass(rawFormula: String): Result<Double> {
        val breakdown = calculateDetailed(rawFormula)
        return breakdown.map { it.totalMolarMass }
    }

    fun calculateDetailed(rawFormula: String): Result<MolarMassResult> {
        val parsed = FormulaParser.parseFormula(rawFormula)
        if (parsed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter a valid chemical formula (e.g. H2O, Ca(OH)2)"))
        }

        var totalMass = 0.0
        val items = mutableListOf<Triple<Element, Int, Double>>()

        for ((symbol, count) in parsed) {
            val element = PeriodicTableRepository.getBySymbol(symbol)
                ?: return Result.failure(IllegalArgumentException("Unknown chemical symbol: '$symbol'"))
            val weight = element.atomicWeight * count
            totalMass += weight
            items.add(Triple(element, count, weight))
        }

        if (totalMass <= 0.0) {
            return Result.failure(IllegalStateException("Calculated mass is zero"))
        }

        val contributions = items.map { (elem, count, weight) ->
            ElementContribution(
                element = elem,
                count = count,
                totalWeight = weight,
                percentage = (weight / totalMass) * 100.0
            )
        }.sortedByDescending { it.percentage }

        return Result.success(
            MolarMassResult(
                formula = rawFormula.trim(),
                totalMolarMass = totalMass,
                contributions = contributions
            )
        )
    }

    fun gramsToMoles(grams: Double, molarMass: Double): Double {
        if (molarMass <= 0) return 0.0
        return grams / molarMass
    }

    fun molesToGrams(moles: Double, molarMass: Double): Double {
        return moles * molarMass
    }

    fun molesToMolecules(moles: Double): Double {
        return moles * AVOGADRO_CONSTANT
    }

    fun moleculesToMoles(molecules: Double): Double {
        return molecules / AVOGADRO_CONSTANT
    }
}
