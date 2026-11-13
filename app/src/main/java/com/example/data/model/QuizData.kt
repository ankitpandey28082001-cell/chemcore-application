package com.example.data.model

data class QuizQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object QuizRepository {

    val questions = listOf(
        QuizQuestion(
            id = 1,
            category = "Periodic Trends",
            question = "Which element has the highest electronegativity on the Pauling scale?",
            options = listOf("Oxygen (O)", "Fluorine (F)", "Chlorine (Cl)", "Helium (He)"),
            correctIndex = 1,
            explanation = "Fluorine has the highest electronegativity (3.98) due to its high effective nuclear charge and small atomic radius."
        ),
        QuizQuestion(
            id = 2,
            category = "Periodic Trends",
            question = "What happens to atomic radius as you move from left to right across a period?",
            options = listOf("Increases", "Decreases", "Remains constant", "Fluctuates randomly"),
            correctIndex = 1,
            explanation = "Atomic radius decreases across a period because the increasing nuclear charge (more protons) pulls the electron cloud closer."
        ),
        QuizQuestion(
            id = 3,
            category = "Acids & Bases",
            question = "What is the pH of a 0.001 M hydrochloric acid (HCl) solution at 25°C?",
            options = listOf("pH = 1", "pH = 2", "pH = 3", "pH = 11"),
            correctIndex = 2,
            explanation = "HCl is a strong acid that dissociates completely: [H⁺] = 10⁻³ M. Therefore pH = -log₁₀(10⁻³) = 3."
        ),
        QuizQuestion(
            id = 4,
            category = "Acids & Bases",
            question = "Which of the following compounds forms an alkaline (basic) solution when dissolved in water?",
            options = listOf("NH₄Cl", "CH₃COOH", "NaHCO₃", "CO₂"),
            correctIndex = 2,
            explanation = "NaHCO₃ (sodium bicarbonate) hydrolyzes in water to form OH⁻ ions through HCO₃⁻ + H₂O ⇌ H₂CO₃ + OH⁻, producing a mildly basic pH around 8.4-9.0."
        ),
        QuizQuestion(
            id = 5,
            category = "Chemical Reactions",
            question = "What type of reaction is: 2 H₂ + O₂ → 2 H₂O?",
            options = listOf("Decomposition", "Synthesis (Combination)", "Single Replacement", "Double Replacement"),
            correctIndex = 1,
            explanation = "Two simpler reactant substances combine to form a single chemical compound (water)."
        ),
        QuizQuestion(
            id = 6,
            category = "Chemical Reactions",
            question = "In the reaction Zn + CuSO₄ → ZnSO₄ + Cu, what is being oxidized?",
            options = listOf("Zinc (Zn)", "Copper (Cu²⁺)", "Sulfate (SO₄²⁻)", "Water (H₂O)"),
            correctIndex = 0,
            explanation = "Zinc loses 2 electrons (Zn → Zn²⁺ + 2e⁻), meaning its oxidation state increases from 0 to +2 (oxidation is loss of electrons: OIL RIG)."
        ),
        QuizQuestion(
            id = 7,
            category = "Stoichiometry & Moles",
            question = "What is the approximate molar mass of Calcium Carbonate (CaCO₃)?",
            options = listOf("68.1 g/mol", "100.1 g/mol", "120.4 g/mol", "84.0 g/mol"),
            correctIndex = 1,
            explanation = "Molar mass = Ca (40.08) + C (12.01) + 3 × O (16.00 × 3 = 48.00) = 100.09 g/mol."
        ),
        QuizQuestion(
            id = 8,
            category = "Bonding & Structure",
            question = "What type of chemical bond involves the sharing of electron pairs between atoms?",
            options = listOf("Ionic bond", "Covalent bond", "Metallic bond", "Hydrogen bond"),
            correctIndex = 1,
            explanation = "A covalent bond forms when nonmetal atoms share valence electron pairs to achieve stable outer shell octets."
        ),
        QuizQuestion(
            id = 9,
            category = "Solutions & Solubility",
            question = "Which of the following salts is INSOLUBLE in water according to solubility rules?",
            options = listOf("NaNO₃", "KCl", "AgCl", "Ca(NO₃)₂"),
            correctIndex = 2,
            explanation = "Most chlorides are soluble, except those of Ag⁺, Pb²⁺, and Hg₂²⁺. Silver chloride (AgCl) forms an insoluble white precipitate."
        ),
        QuizQuestion(
            id = 10,
            category = "Gases & Physical States",
            question = "Which noble gas is the most abundant in Earth's atmosphere?",
            options = listOf("Helium", "Neon", "Argon", "Krypton"),
            correctIndex = 2,
            explanation = "Argon makes up roughly 0.93% (almost 1%) of Earth's atmosphere, vastly more than helium or neon."
        )
    )
}
