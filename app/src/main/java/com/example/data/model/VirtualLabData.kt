package com.example.data.model

import androidx.compose.ui.graphics.Color

data class Reagent(
    val id: String,
    val name: String,
    val formula: String,
    val initialColor: Color,
    val physicalState: String, // "Liquid", "Solid", "Solution"
    val description: String,
    val category: String // "Acid", "Base", "Salt", "Metal", "Indicator"
)

data class ReactionOutcome(
    val reactant1Id: String,
    val reactant2Id: String,
    val balancedEquation: String,
    val finalLiquidColor: Color,
    val hasGasBubbles: Boolean,
    val hasPrecipitate: Boolean,
    val precipitateColor: Color = Color.Transparent,
    val precipitateName: String = "",
    val gasName: String = "",
    val enthalpyType: String, // "Exothermic (Heats up)", "Endothermic (Cools down)", "Athermal"
    val observation: String,
    val scientificExplanation: String,
    val hazardNote: String
)

object VirtualLabRepository {

    val availableReagents = listOf(
        Reagent("HCl", "Hydrochloric Acid", "HCl (aq)", Color(0xFFE2E8F0), "Liquid", "Strong, pungent mineral acid", "Acid"),
        Reagent("NaOH", "Sodium Hydroxide", "NaOH (aq)", Color(0xFFE2E8F0), "Liquid", "Strong caustic alkaline base", "Base"),
        Reagent("AgNO3", "Silver Nitrate", "AgNO₃ (aq)", Color(0xFFF1F5F9), "Liquid", "Soluble silver salt, stains skin dark", "Salt"),
        Reagent("NaCl", "Sodium Chloride", "NaCl (aq)", Color(0xFFF8FAFC), "Liquid", "Everyday saline solution", "Salt"),
        Reagent("CuSO4", "Copper(II) Sulfate", "CuSO₄ (aq)", Color(0xFF38BDF8), "Liquid", "Bright azure blue hydrated copper solution", "Salt"),
        Reagent("NH3", "Aqueous Ammonia", "NH₃ (aq)", Color(0xFFF1F5F9), "Liquid", "Pungent weak base, strong ligand", "Base"),
        Reagent("KI", "Potassium Iodide", "KI (aq)", Color(0xFFF8FAFC), "Liquid", "Colorless ionic iodide salt solution", "Salt"),
        Reagent("PbNO3", "Lead(II) Nitrate", "Pb(NO₃)₂ (aq)", Color(0xFFF1F5F9), "Liquid", "Soluble heavy metal nitrate salt", "Salt"),
        Reagent("NaHCO3", "Sodium Bicarbonate", "NaHCO₃", Color(0xFFFFFFFF), "Solid", "Baking soda powder", "Salt"),
        Reagent("CH3COOH", "Acetic Acid", "CH₃COOH", Color(0xFFF8FAFC), "Liquid", "Weak organic acid found in vinegar", "Acid"),
        Reagent("Zn", "Zinc Granules", "Zn (s)", Color(0xFF94A3B8), "Solid", "Silvery reactive transition metal", "Metal"),
        Reagent("FeCl3", "Iron(III) Chloride", "FeCl₃ (aq)", Color(0xFFFBBF24), "Liquid", "Golden-amber ferric chloride solution", "Salt"),
        Reagent("KSCN", "Potassium Thiocyanate", "KSCN (aq)", Color(0xFFF8FAFC), "Liquid", "Colorless pseudohalide salt solution", "Salt")
    )

    val reactions = listOf(
        ReactionOutcome(
            reactant1Id = "AgNO3", reactant2Id = "NaCl",
            balancedEquation = "AgNO₃ (aq) + NaCl (aq) → AgCl (s)↓ + NaNO₃ (aq)",
            finalLiquidColor = Color(0xFFE2E8F0),
            hasGasBubbles = false,
            hasPrecipitate = true,
            precipitateColor = Color(0xFFFFFFFF),
            precipitateName = "White Silver Chloride (AgCl)",
            enthalpyType = "Mild Exothermic",
            observation = "Instant formation of a thick, curdy white precipitate.",
            scientificExplanation = "Ag⁺ and Cl⁻ ions combine to form insoluble Silver Chloride due to high lattice energy overcoming hydration enthalpy (Ksp = 1.8 × 10⁻¹⁰).",
            hazardNote = "Silver salts are sensitive to light and cause temporary skin staining."
        ),
        ReactionOutcome(
            reactant1Id = "KI", reactant2Id = "PbNO3",
            balancedEquation = "Pb(NO₃)₂ (aq) + 2 KI (aq) → PbI₂ (s)↓ + 2 KNO₃ (aq)",
            finalLiquidColor = Color(0xFFFEF08A),
            hasGasBubbles = false,
            hasPrecipitate = true,
            precipitateColor = Color(0xFFFACC15),
            precipitateName = "Golden Yellow Lead(II) Iodide (PbI₂)",
            enthalpyType = "Athermal",
            observation = "Striking, instant bright canary-yellow precipitate ('Golden Rain' reaction).",
            scientificExplanation = "Classic demonstration of double displacement. Lead(II) ions bond with iodide ions forming highly insoluble PbI₂ crystals with low solubility product.",
            hazardNote = "Toxic heavy metal waste. Must be disposed of properly."
        ),
        ReactionOutcome(
            reactant1Id = "HCl", reactant2Id = "NaHCO3",
            balancedEquation = "HCl (aq) + NaHCO₃ (s) → NaCl (aq) + H₂O (l) + CO₂ (g)↑",
            finalLiquidColor = Color(0xFFE2E8F0),
            hasGasBubbles = true,
            hasPrecipitate = false,
            gasName = "Carbon Dioxide (CO₂)",
            enthalpyType = "Endothermic (Slight cooling)",
            observation = "Vigorous effervescence and rapid bubbling froth releasing gas.",
            scientificExplanation = "Bicarbonate acts as a Brønsted base accepting H⁺ to form carbonic acid (H₂CO₃), which rapidly decomposes into liquid water and gaseous carbon dioxide.",
            hazardNote = "Gas buildup can pressurize sealed containers."
        ),
        ReactionOutcome(
            reactant1Id = "CH3COOH", reactant2Id = "NaHCO3",
            balancedEquation = "CH₃COOH (aq) + NaHCO₃ (s) → CH₃COONa (aq) + H₂O (l) + CO₂ (g)↑",
            finalLiquidColor = Color(0xFFE2E8F0),
            hasGasBubbles = true,
            hasPrecipitate = false,
            gasName = "Carbon Dioxide (CO₂)",
            enthalpyType = "Endothermic (Noticeably cool)",
            observation = "Classic volcano foaming effervescence with fizzing sound.",
            scientificExplanation = "Acetic acid donates a proton to bicarbonate, forming sodium acetate, water, and expanding CO₂ gas.",
            hazardNote = "Safe educational reaction; endothermic temperature drop feels cool to touch."
        ),
        ReactionOutcome(
            reactant1Id = "CuSO4", reactant2Id = "NH3",
            balancedEquation = "CuSO₄ (aq) + 4 NH₃ (aq) → [Cu(NH₃)₄]SO₄ (aq)",
            finalLiquidColor = Color(0xFF1D4ED8), // Deep royal sapphire blue
            hasGasBubbles = false,
            hasPrecipitate = false,
            enthalpyType = "Exothermic",
            observation = "The light blue solution instantly transforms into a breathtaking, deep sapphire-blue ink color.",
            scientificExplanation = "Ammonia molecules displace water ligands in the octahedral copper complex to form the tetraamminecopper(II) coordination complex [Cu(NH₃)₄]²⁺ with large crystal field splitting.",
            hazardNote = "Ammonia vapor has pungent fumes; work in ventilated space."
        ),
        ReactionOutcome(
            reactant1Id = "HCl", reactant2Id = "Zn",
            balancedEquation = "Zn (s) + 2 HCl (aq) → ZnCl₂ (aq) + H₂ (g)↑",
            finalLiquidColor = Color(0xFFE2E8F0),
            hasGasBubbles = true,
            hasPrecipitate = false,
            gasName = "Hydrogen Gas (H₂)",
            enthalpyType = "Exothermic (Warmer)",
            observation = "Rapid stream of tiny bubbles rising vigorously from zinc metal surface.",
            scientificExplanation = "Zinc is above hydrogen in the electrochemical activity series. Zinc is oxidized (Zn → Zn²⁺ + 2e⁻) and hydrogen ions are reduced (2H⁺ + 2e⁻ → H₂).",
            hazardNote = "Hydrogen gas is flammable; keep away from open flames."
        ),
        ReactionOutcome(
            reactant1Id = "FeCl3", reactant2Id = "KSCN",
            balancedEquation = "FeCl₃ (aq) + 3 KSCN (aq) ⇌ Fe(SCN)₃ (aq) + 3 KCl (aq)",
            finalLiquidColor = Color(0xFF991B1B), // Blood red
            hasGasBubbles = false,
            hasPrecipitate = false,
            enthalpyType = "Mild Exothermic",
            observation = "Solution instantly turns deep intense blood-red (used in classic movie fake blood).",
            scientificExplanation = "Formation of iron(III) thiocyanate coordination complexes with strong charge-transfer absorption bands in the green spectrum, giving vivid red transmission.",
            hazardNote = "High staining potential."
        ),
        ReactionOutcome(
            reactant1Id = "HCl", reactant2Id = "NaOH",
            balancedEquation = "HCl (aq) + NaOH (aq) → NaCl (aq) + H₂O (l)",
            finalLiquidColor = Color(0xFFE2E8F0),
            hasGasBubbles = false,
            hasPrecipitate = false,
            enthalpyType = "Exothermic (Strong heat release ΔH = -57.3 kJ/mol)",
            observation = "No color change in clear liquids, but noticeable release of thermal heat.",
            scientificExplanation = "Classic strong acid - strong base neutralization. Hydronium and hydroxide ions combine: H⁺ + OH⁻ → H₂O.",
            hazardNote = "Mixing concentrated acids and bases can boil violently."
        )
    )

    fun findReaction(id1: String, id2: String): ReactionOutcome? {
        return reactions.firstOrNull {
            (it.reactant1Id == id1 && it.reactant2Id == id2) ||
            (it.reactant1Id == id2 && it.reactant2Id == id1)
        }
    }
}
