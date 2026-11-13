package com.example

import com.example.calculator.ChemicalEquationBalancer
import com.example.calculator.FormulaParser
import com.example.calculator.MolarMassCalculator
import com.example.calculator.SolutionsCalculator
import com.example.data.db.AppDatabase
import com.example.util.TimeUtils
import org.junit.Assert.*
import org.junit.Test
import java.util.TimeZone

class ExampleUnitTest {

    @Test
    fun formulaParser_parsesSimpleAndParentheses() {
        val water = FormulaParser.parseFormula("H2O")
        assertEquals(2, water["H"])
        assertEquals(1, water["O"])

        val caOh2 = FormulaParser.parseFormula("Ca(OH)2")
        assertEquals(1, caOh2["Ca"])
        assertEquals(2, caOh2["O"])
        assertEquals(2, caOh2["H"])

        val fe2So43 = FormulaParser.parseFormula("Fe2(SO4)3")
        assertEquals(2, fe2So43["Fe"])
        assertEquals(3, fe2So43["S"])
        assertEquals(12, fe2So43["O"])
    }

    @Test
    fun chemicalEquationBalancer_balancesReactions() {
        val h2oRes = ChemicalEquationBalancer.balance("H2 + O2 -> H2O")
        assertTrue(h2oRes.isSuccess)
        val h2oBal = h2oRes.getOrThrow()
        assertEquals("2 H2 + O2 → 2 H2O", h2oBal.balancedEquation)

        val propaneRes = ChemicalEquationBalancer.balance("C3H8 + O2 -> CO2 + H2O")
        assertTrue(propaneRes.isSuccess)
        val propaneBal = propaneRes.getOrThrow()
        assertEquals("C3H8 + 5 O2 → 3 CO2 + 4 H2O", propaneBal.balancedEquation)

        val rustRes = ChemicalEquationBalancer.balance("Fe + O2 -> Fe2O3")
        assertTrue(rustRes.isSuccess)
        val rustBal = rustRes.getOrThrow()
        assertEquals("4 Fe + 3 O2 → 2 Fe2O3", rustBal.balancedEquation)
    }

    @Test
    fun molarMassCalculator_computesAccurateMass() {
        val h2o = MolarMassCalculator.calculateMolarMass("H2O")
        assertTrue(h2o.isSuccess)
        assertEquals(18.015, h2o.getOrThrow(), 0.05)

        val caOh2 = MolarMassCalculator.calculateMolarMass("Ca(OH)2")
        assertTrue(caOh2.isSuccess)
        assertEquals(74.09, caOh2.getOrThrow(), 0.1)
    }

    @Test
    fun solutionsCalculator_convertsPhCorrectly() {
        val neutral = SolutionsCalculator.calculateFromPh(7.0)
        assertEquals(7.0, neutral.ph, 0.01)
        assertEquals(7.0, neutral.poh, 0.01)
        assertEquals(1e-7, neutral.hConcentration, 1e-8)

        val acid = SolutionsCalculator.calculateFromPh(2.0)
        assertEquals(12.0, acid.poh, 0.01)
        assertEquals(1e-2, acid.hConcentration, 1e-3)
    }

    @Test
    fun timeUtils_formatsAccurateIndianStandardTime() {
        TimeUtils.initIndianStandardTime()
        assertEquals("Asia/Kolkata", TimeZone.getDefault().id)

        // Epoch 0 (1970-01-01 00:00:00 UTC) in IST (UTC+5:30) is 05:30 AM on 01 Jan 1970
        val istFormatted = TimeUtils.formatIstTime(0L)
        assertEquals("05:30 am", istFormatted.lowercase())

        val istDateTime = TimeUtils.formatIstDateTime(0L)
        assertTrue(istDateTime.contains("01 Jan 1970"))
        assertTrue(istDateTime.contains("05:30 AM") || istDateTime.contains("05:30 am"))
    }

    @Test
    fun passwordHash_isDeterministic() {
        val hash1 = AppDatabase.hashPassword("Password@123")
        val hash2 = AppDatabase.hashPassword("Password@123")
        val hash3 = AppDatabase.hashPassword("DifferentPassword")
        assertEquals(hash1, hash2)
        assertNotEquals(hash1, hash3)
        assertEquals(64, hash1.length) // SHA-256 produces 64 hex characters
    }
}
