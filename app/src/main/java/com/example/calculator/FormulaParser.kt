package com.example.calculator

object FormulaParser {

    /**
     * Parses a chemical formula (e.g. "Ca(OH)2", "CuSO4*5H2O", "Fe2(SO4)3", "H2O")
     * into a map of element symbol to count (e.g., {"Ca": 1, "O": 2, "H": 2}).
     */
    fun parseFormula(rawFormula: String): Map<String, Int> {
        val formula = rawFormula.trim().replace("·", "*").replace(" ", "")
        if (formula.isEmpty()) return emptyMap()

        // Handle hydrates like CuSO4*5H2O
        if (formula.contains("*")) {
            val parts = formula.split("*")
            val baseCounts = parseSingleFormula(parts[0])
            val totalCounts = baseCounts.toMutableMap()
            for (i in 1 until parts.size) {
                val hydratePart = parts[i]
                var multiplier = 1
                var hydrateFormula = hydratePart
                var idx = 0
                while (idx < hydratePart.length && hydratePart[idx].isDigit()) {
                    idx++
                }
                if (idx > 0) {
                    multiplier = hydratePart.substring(0, idx).toIntOrNull() ?: 1
                    hydrateFormula = hydratePart.substring(idx)
                }
                val waterCounts = parseSingleFormula(hydrateFormula)
                for ((elem, count) in waterCounts) {
                    totalCounts[elem] = (totalCounts[elem] ?: 0) + (count * multiplier)
                }
            }
            return totalCounts
        }

        return parseSingleFormula(formula)
    }

    private fun parseSingleFormula(formula: String): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        var i = 0
        val n = formula.length

        fun parseGroup(): Map<String, Int> {
            val groupCounts = mutableMapOf<String, Int>()

            while (i < n) {
                val c = formula[i]

                if (c == '(' || c == '[' || c == '{') {
                    i++
                    val inner = parseGroup()
                    // Check for closing bracket
                    if (i < n && (formula[i] == ')' || formula[i] == ']' || formula[i] == '}')) {
                        i++
                    }
                    // Read number multiplier after group
                    var mult = 0
                    while (i < n && formula[i].isDigit()) {
                        mult = mult * 10 + (formula[i] - '0')
                        i++
                    }
                    if (mult == 0) mult = 1
                    for ((elem, count) in inner) {
                        groupCounts[elem] = (groupCounts[elem] ?: 0) + count * mult
                    }
                } else if (c == ')' || c == ']' || c == '}') {
                    break
                } else if (c.isUpperCase()) {
                    // Start of element symbol
                    val start = i
                    i++
                    while (i < n && formula[i].isLowerCase()) {
                        i++
                    }
                    val elem = formula.substring(start, i)
                    var count = 0
                    while (i < n && formula[i].isDigit()) {
                        count = count * 10 + (formula[i] - '0')
                        i++
                    }
                    if (count == 0) count = 1
                    groupCounts[elem] = (groupCounts[elem] ?: 0) + count
                } else {
                    i++ // skip non-standard characters
                }
            }
            return groupCounts
        }

        return parseGroup()
    }
}
