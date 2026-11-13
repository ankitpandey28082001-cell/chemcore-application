package com.example.data.model

val elementsPart3 = listOf(
    Element(87, "Fr", "Francium", 223.0, ElementCategory.ALKALI_METAL, 7, 1, "s", "[Rn] 7s¹", 0.7, 300.0, 950.0, 1.87, "Solid", "1939", "Marguerite Perey", "Second rarest element in the Earth's crust; highly radioactive alkali metal.", listOf(2, 8, 18, 32, 18, 8, 1), listOf(1)),
    Element(88, "Ra", "Radium", 226.0, ElementCategory.ALKALINE_EARTH, 7, 2, "s", "[Rn] 7s²", 0.9, 973.0, 2010.0, 5.5, "Solid", "1898", "Marie Curie & Pierre Curie", "Intensely radioactive, famously discovered in uraninite pitchblende by Marie Curie.", listOf(2, 8, 18, 32, 18, 8, 2), listOf(2)),
    Element(92, "U", "Uranium", 238.03, ElementCategory.ACTINIDE, 7, 3, "f", "[Rn] 5f³ 6d¹ 7s²", 1.38, 1405.3, 4404.0, 19.1, "Solid", "1789", "Martin Heinrich Klaproth", "Fissile isotope U-235 is the primary fuel for nuclear reactors and power generation.", listOf(2, 8, 18, 32, 21, 9, 2), listOf(3, 4, 6)),
    Element(94, "Pu", "Plutonium", 244.0, ElementCategory.ACTINIDE, 7, 3, "f", "[Rn] 5f⁶ 7s²", 1.28, 912.5, 3501.0, 19.816, "Solid", "1940", "Glenn T. Seaborg", "Synthetic actinide used in radioisotope thermoelectric generators on NASA deep space probes.", listOf(2, 8, 18, 32, 24, 8, 2), listOf(3, 4, 6)),
    Element(118, "Og", "Oganesson", 294.0, ElementCategory.NOBLE_GAS, 7, 18, "p", "[Rn] 5f¹⁴ 6d¹⁰ 7s² 7p⁶", null, 325.0, 350.0, 5.0, "Solid", "2002", "Joint Institute for Nuclear Research", "Heaviest known chemical element, atomic number 118, completing period 7 of the periodic table.", listOf(2, 8, 18, 32, 32, 18, 8), listOf(0))
)

object PeriodicTableRepository {
    val allElements: List<Element> by lazy {
        (elementsPart1 + elementsPart2 + elementsPart3).sortedBy { it.atomicNumber }
    }

    fun getBySymbol(symbol: String): Element? {
        return allElements.firstOrNull { it.symbol.equals(symbol, ignoreCase = true) }
    }

    fun getByAtomicNumber(number: Int): Element? {
        return allElements.firstOrNull { it.atomicNumber == number }
    }

    fun search(query: String): List<Element> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return allElements
        return allElements.filter {
            it.name.lowercase().contains(q) ||
            it.symbol.lowercase().contains(q) ||
            it.atomicNumber.toString() == q ||
            it.category.displayName.lowercase().contains(q)
        }
    }
}
