package com.example.data.model

data class PolyatomicIon(
    val name: String,
    val formula: String,
    val charge: String,
    val commonExamples: String
)

data class SolubilityRule(
    val category: String,
    val rule: String,
    val exceptions: String,
    val isGenerallySoluble: Boolean
)

data class ChemConstant(
    val symbol: String,
    val name: String,
    val value: String,
    val unit: String,
    val description: String
)

object ReferenceRepository {

    val polyatomicIons = listOf(
        PolyatomicIon("Nitrate", "NO₃⁻", "-1", "AgNO₃, KNO₃, HNO₃"),
        PolyatomicIon("Nitrite", "NO₂⁻", "-1", "NaNO₂, KNO₂"),
        PolyatomicIon("Sulfate", "SO₄²⁻", "-2", "CuSO₄, H₂SO₄, Na₂SO₄"),
        PolyatomicIon("Sulfite", "SO₃²⁻", "-2", "Na₂SO₃"),
        PolyatomicIon("Hydroxide", "OH⁻", "-1", "NaOH, KOH, Ca(OH)₂"),
        PolyatomicIon("Ammonium", "NH₄⁺", "+1", "NH₄Cl, (NH₄)₂SO₄"),
        PolyatomicIon("Carbonate", "CO₃²⁻", "-2", "CaCO₃, Na₂CO₃"),
        PolyatomicIon("Bicarbonate", "HCO₃⁻", "-1", "NaHCO₃, KHCO₃"),
        PolyatomicIon("Phosphate", "PO₄³⁻", "-3", "H₃PO₄, Na₃PO₄, Ca₃(PO₄)₂"),
        PolyatomicIon("Hydrogen Phosphate", "HPO₄²⁻", "-2", "K₂HPO₄"),
        PolyatomicIon("Dihydrogen Phosphate", "H₂PO₄⁻", "-1", "KH₂PO₄"),
        PolyatomicIon("Cyanide", "CN⁻", "-1", "KCN, NaCN"),
        PolyatomicIon("Thiocyanate", "SCN⁻", "-1", "KSCN, Fe(SCN)₃"),
        PolyatomicIon("Acetate", "C₂H₃O₂⁻ / CH₃COO⁻", "-1", "CH₃COONa, Pb(C₂H₃O₂)₂"),
        PolyatomicIon("Permanganate", "MnO₄⁻", "-1", "KMnO₄ (strong oxidizer)"),
        PolyatomicIon("Chromate", "CrO₄²⁻", "-2", "K₂CrO₄ (yellow)"),
        PolyatomicIon("Dichromate", "Cr₂O₇²⁻", "-2", "K₂Cr₂O₇ (orange)"),
        PolyatomicIon("Perchlorate", "ClO₄⁻", "-1", "KClO₄, HClO₄"),
        PolyatomicIon("Chlorate", "ClO₃⁻", "-1", "KClO₃"),
        PolyatomicIon("Hypochlorite", "ClO⁻", "-1", "NaClO (household bleach)")
    )

    val solubilityRules = listOf(
        SolubilityRule(
            category = "Group 1 Alkali & Ammonium",
            rule = "Compounds of Li⁺, Na⁺, K⁺, Rb⁺, Cs⁺, and NH₄⁺ are soluble.",
            exceptions = "Virtually no exceptions in aqueous chemistry.",
            isGenerallySoluble = true
        ),
        SolubilityRule(
            category = "Nitrates & Acetates",
            rule = "All nitrates (NO₃⁻) and acetates (CH₃COO⁻) are soluble.",
            exceptions = "None commonly encountered.",
            isGenerallySoluble = true
        ),
        SolubilityRule(
            category = "Chlorides, Bromides & Iodides",
            rule = "Halide salts of Cl⁻, Br⁻, and I⁻ are soluble.",
            exceptions = "Insoluble with Ag⁺, Pb²⁺, and Hg₂²⁺ (e.g. AgCl, PbI₂ precipitate!).",
            isGenerallySoluble = true
        ),
        SolubilityRule(
            category = "Sulfates (SO₄²⁻)",
            rule = "Most sulfate salts are soluble in water.",
            exceptions = "Insoluble with Ba²⁺, Pb²⁺, Ca²⁺, and Sr²⁺ (e.g. BaSO₄, PbSO₄).",
            isGenerallySoluble = true
        ),
        SolubilityRule(
            category = "Carbonates & Phosphates",
            rule = "Carbonates (CO₃²⁻) and phosphates (PO₄³⁻) are INSOLUBLE.",
            exceptions = "Soluble only when bonded to Group 1 cations or NH₄⁺.",
            isGenerallySoluble = false
        ),
        SolubilityRule(
            category = "Hydroxides & Oxides",
            rule = "Hydroxides (OH⁻) and metal oxides are INSOLUBLE.",
            exceptions = "Soluble with Group 1 alkali metals, Ba²⁺, and Sr²⁺; Ca(OH)₂ is slightly soluble.",
            isGenerallySoluble = false
        ),
        SolubilityRule(
            category = "Sulfides (S²⁻)",
            rule = "Most metal sulfides are INSOLUBLE.",
            exceptions = "Soluble with Group 1, Group 2, and ammonium cations.",
            isGenerallySoluble = false
        )
    )

    val activitySeries = listOf(
        "Lithium (Li)" to "Reacts violently with cold water, releasing H₂ gas",
        "Potassium (K)" to "Reacts violently with water with violet flame",
        "Barium (Ba)" to "Reacts vigorously with water",
        "Calcium (Ca)" to "Reacts steadily with cold water",
        "Sodium (Na)" to "Reacts rapidly with water, yellow flame",
        "Magnesium (Mg)" to "Reacts slowly with cold water, rapidly with steam or acids",
        "Aluminium (Al)" to "Passivated by oxide layer; dissolves in strong acids/bases",
        "Zinc (Zn)" to "Displaces H₂ from dilute mineral acids (HCl, H₂SO₄)",
        "Iron (Fe)" to "Displaces H₂ from acids; corrodes in moist air",
        "Nickel (Ni)" to "Reacts slowly with acids",
        "Tin (Sn)" to "Reacts very slowly with dilute acids",
        "Lead (Pb)" to "Reacts slowly with hot acids",
        "HYDROGEN (H₂)" to "Standard Reference Potential (0.00 V)",
        "Copper (Cu)" to "Does not displace H₂ from acids; oxidized by HNO₃",
        "Silver (Ag)" to "Unreactive to non-oxidizing acids, noble metal",
        "Platinum (Pt)" to "Highly inert, dissolves only in aqua regia",
        "Gold (Au)" to "Extremely unreactive, king of noble metals"
    )

    val constants = listOf(
        ChemConstant("R", "Ideal Gas Constant", "8.31446", "J / (mol · K)", "Used in thermodynamics, kinetics, and electrochemistry equations"),
        ChemConstant("R_atm", "Gas Constant (atm)", "0.082057", "L · atm / (mol · K)", "Convenient for PV = nRT calculations with atmospheres and liters"),
        ChemConstant("N_A", "Avogadro's Number", "6.02214 × 10²³", "mol⁻¹", "Number of constituent particles (atoms/molecules) per mole"),
        ChemConstant("F", "Faraday Constant", "96,485.3", "C / mol e⁻", "Electric charge carried per mole of electrons (N_A × e)"),
        ChemConstant("h", "Planck's Constant", "6.62607 × 10⁻³⁴", "J · s", "Fundamental quantum of action relating photon energy to frequency (E = hν)"),
        ChemConstant("c", "Speed of Light", "2.99792 × 10⁸", "m / s", "Universal speed of electromagnetic radiation in vacuum"),
        ChemConstant("V_m", "Molar Volume at STP", "22.414", "L / mol", "Volume occupied by one mole of an ideal gas at 0°C and 1 atm")
    )
}
