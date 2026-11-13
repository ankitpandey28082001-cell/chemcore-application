package com.example.calculator

import com.example.data.model.PeriodicTableRepository

data class BalancedReactionResult(
    val originalEquation: String,
    val balancedEquation: String,
    val reactants: List<CompoundWithCoeff>,
    val products: List<CompoundWithCoeff>,
    val reactionType: String,
    val elementConservation: Map<String, AtomConservation>,
    val explanation: String
)

data class CompoundWithCoeff(
    val coefficient: Int,
    val formula: String,
    val molarMass: Double = 0.0
)

data class AtomConservation(
    val reactantCount: Int,
    val productCount: Int,
    val isBalanced: Boolean
)

object ChemicalEquationBalancer {

    fun balance(equation: String): Result<BalancedReactionResult> {
        val cleanEq = equation.trim()
        val separator = when {
            cleanEq.contains("->") -> "->"
            cleanEq.contains("→") -> "→"
            cleanEq.contains("=") -> "="
            else -> return Result.failure(IllegalArgumentException("Equation must contain '->' or '='"))
        }

        val parts = cleanEq.split(separator)
        if (parts.size != 2) {
            return Result.failure(IllegalArgumentException("Invalid equation format: must have reactants and products"))
        }

        val rawReactants = parts[0].split("+").map { stripLeadingCoeff(it.trim()) }.filter { it.isNotEmpty() }
        val rawProducts = parts[1].split("+").map { stripLeadingCoeff(it.trim()) }.filter { it.isNotEmpty() }

        if (rawReactants.isEmpty() || rawProducts.isEmpty()) {
            return Result.failure(IllegalArgumentException("Both reactants and products are required"))
        }

        val allSpecies = rawReactants + rawProducts
        val numReactants = rawReactants.size
        val totalSpecies = allSpecies.size

        // Parse atoms for each compound
        val speciesAtomMaps = allSpecies.map { FormulaParser.parseFormula(it) }
        val allElements = speciesAtomMaps.flatMap { it.keys }.distinct().sorted()

        if (allElements.isEmpty()) {
            return Result.failure(IllegalArgumentException("No valid chemical elements detected"))
        }

        // Try small integer search first (extremely fast and always finds minimal coprime integers for typical reactions)
        val coefficients = solveCoefficients(speciesAtomMaps, numReactants, allElements)
            ?: return Result.failure(IllegalStateException("Could not balance equation. Please check chemical formulas."))

        val reactantList = rawReactants.mapIndexed { idx, formula ->
            CompoundWithCoeff(
                coefficient = coefficients[idx],
                formula = formula,
                molarMass = MolarMassCalculator.calculateMolarMass(formula).getOrDefault(0.0)
            )
        }

        val productList = rawProducts.mapIndexed { idx, formula ->
            val coeffIndex = numReactants + idx
            CompoundWithCoeff(
                coefficient = coefficients[coeffIndex],
                formula = formula,
                molarMass = MolarMassCalculator.calculateMolarMass(formula).getOrDefault(0.0)
            )
        }

        // Build string representation
        val balancedStr = buildString {
            append(reactantList.joinToString(" + ") { if (it.coefficient > 1) "${it.coefficient} ${it.formula}" else it.formula })
            append(" → ")
            append(productList.joinToString(" + ") { if (it.coefficient > 1) "${it.coefficient} ${it.formula}" else it.formula })
        }

        // Verify element conservation
        val conservation = mutableMapOf<String, AtomConservation>()
        for (elem in allElements) {
            var rCount = 0
            for (i in 0 until numReactants) {
                rCount += (speciesAtomMaps[i][elem] ?: 0) * coefficients[i]
            }
            var pCount = 0
            for (i in numReactants until totalSpecies) {
                pCount += (speciesAtomMaps[i][elem] ?: 0) * coefficients[i]
            }
            conservation[elem] = AtomConservation(
                reactantCount = rCount,
                productCount = pCount,
                isBalanced = rCount == pCount
            )
        }

        val reactionType = determineReactionType(reactantList, productList)

        val explanation = buildString {
            append("Reaction Type: $reactionType\n")
            append("Law of Conservation of Mass verified: All atoms on reactant side equal product side.\n")
            append(allElements.joinToString(", ") { elem ->
                val c = conservation[elem]!!
                "$elem: ${c.reactantCount} on both sides"
            })
        }

        return Result.success(
            BalancedReactionResult(
                originalEquation = cleanEq,
                balancedEquation = balancedStr,
                reactants = reactantList,
                products = productList,
                reactionType = reactionType,
                elementConservation = conservation,
                explanation = explanation
            )
        )
    }

    private fun stripLeadingCoeff(formula: String): String {
        var idx = 0
        while (idx < formula.length && formula[idx].isDigit()) {
            idx++
        }
        return formula.substring(idx).trim()
    }

    private fun solveCoefficients(
        speciesAtomMaps: List<Map<String, Int>>,
        numReactants: Int,
        allElements: List<String>
    ): List<Int>? {
        val totalSpecies = speciesAtomMaps.size

        // Build matrix where column j represents species j
        // Reactants positive, products negative: sum_j a_ij * c_j = 0
        val matrix = Array(allElements.size) { elemIdx ->
            val elem = allElements[elemIdx]
            DoubleArray(totalSpecies) { spIdx ->
                val count = speciesAtomMaps[spIdx][elem] ?: 0
                if (spIdx < numReactants) count.toDouble() else -count.toDouble()
            }
        }

        // Fast DFS search for integer solution up to max coefficient 20
        fun search(index: Int, currentCoeffs: IntArray): IntArray? {
            if (index == totalSpecies) {
                // Check if all element balances evaluate to 0
                for (row in matrix) {
                    var sum = 0.0
                    for (c in 0 until totalSpecies) {
                        sum += row[c] * currentCoeffs[c]
                    }
                    if (Math.abs(sum) > 1e-6) return null
                }
                return currentCoeffs.clone()
            }

            for (coeff in 1..16) {
                currentCoeffs[index] = coeff
                val sol = search(index + 1, currentCoeffs)
                if (sol != null) return sol
            }
            return null
        }

        val initial = IntArray(totalSpecies) { 1 }
        val found = search(0, initial) ?: return null

        // Reduce by greatest common divisor
        var g = found[0]
        for (i in 1 until totalSpecies) {
            g = gcd(g, found[i])
        }
        return found.map { it / g }
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = Math.abs(a)
        var y = Math.abs(b)
        while (y != 0) {
            val t = y
            y = x % y
            x = t
        }
        return if (x == 0) 1 else x
    }

    private fun determineReactionType(
        reactants: List<CompoundWithCoeff>,
        products: List<CompoundWithCoeff>
    ): String {
        val rFormulas = reactants.map { it.formula }
        val pFormulas = products.map { it.formula }

        // Combustion: Hydrocarbon (C, H, possibly O) + O2 -> CO2 + H2O
        if (rFormulas.contains("O2") && pFormulas.contains("CO2") && pFormulas.contains("H2O")) {
            return "Combustion Reaction"
        }

        // Synthesis / Combination: A + B -> AB (multiple reactants form 1 product)
        if (reactants.size >= 2 && products.size == 1) {
            return "Synthesis (Combination)"
        }

        // Decomposition: AB -> A + B (1 reactant breaks into multiple)
        if (reactants.size == 1 && products.size >= 2) {
            return "Decomposition Reaction"
        }

        // Single Replacement: element + compound -> new element + new compound
        val isSingleReplacement = reactants.size == 2 && products.size == 2 &&
                (isElemental(rFormulas[0]) || isElemental(rFormulas[1])) &&
                (isElemental(pFormulas[0]) || isElemental(pFormulas[1]))
        if (isSingleReplacement) {
            return "Single Replacement (Displacement)"
        }

        // Acid-Base Neutralization: contains acid and base -> salt + H2O
        if ((pFormulas.contains("H2O") || pFormulas.contains("HOH")) &&
            (rFormulas.any { it.endsWith("OH") } || rFormulas.any { it.startsWith("H") })) {
            return "Acid-Base Neutralization"
        }

        // Double Replacement: AB + CD -> AD + CB
        if (reactants.size == 2 && products.size == 2) {
            return "Double Replacement (Metathesis)"
        }

        return "Oxidation-Reduction / General Reaction"
    }

    private fun isElemental(formula: String): Boolean {
        val parsed = FormulaParser.parseFormula(formula)
        return parsed.size == 1
    }
}
