package com.example.data.model

val elementsPart1 = listOf(
    Element(
        atomicNumber = 1, symbol = "H", name = "Hydrogen", atomicWeight = 1.008,
        category = ElementCategory.REACTIVE_NONMETAL, period = 1, group = 1, block = "s",
        electronConfiguration = "1s¹", electronegativity = 2.20, meltingPointKelvin = 13.99, boilingPointKelvin = 20.271,
        density = 0.00008988, phaseAtSTP = "Gas", discoveryYear = "1766", discoveredBy = "Henry Cavendish",
        summary = "Most abundant chemical substance in the universe, constituting roughly 75% of all baryonic mass.",
        shells = listOf(1), oxidationStates = listOf(1, -1)
    ),
    Element(
        atomicNumber = 2, symbol = "He", name = "Helium", atomicWeight = 4.0026,
        category = ElementCategory.NOBLE_GAS, period = 1, group = 18, block = "s",
        electronConfiguration = "1s²", electronegativity = null, meltingPointKelvin = 0.95, boilingPointKelvin = 4.222,
        density = 0.0001785, phaseAtSTP = "Gas", discoveryYear = "1868", discoveredBy = "Pierre Janssen & Norman Lockyer",
        summary = "Colorless, odorless, inert noble gas. Second lightest and second most abundant element in the observable universe.",
        shells = listOf(2), oxidationStates = emptyList()
    ),
    Element(
        atomicNumber = 3, symbol = "Li", name = "Lithium", atomicWeight = 6.94,
        category = ElementCategory.ALKALI_METAL, period = 2, group = 1, block = "s",
        electronConfiguration = "[He] 2s¹", electronegativity = 0.98, meltingPointKelvin = 453.65, boilingPointKelvin = 1603.0,
        density = 0.534, phaseAtSTP = "Solid", discoveryYear = "1817", discoveredBy = "Johan August Arfwedson",
        summary = "Soft, silvery-white alkali metal. The least dense of all solid elements, highly reactive with water.",
        shells = listOf(2, 1), oxidationStates = listOf(1)
    ),
    Element(
        atomicNumber = 4, symbol = "Be", name = "Beryllium", atomicWeight = 9.0122,
        category = ElementCategory.ALKALINE_EARTH, period = 2, group = 2, block = "s",
        electronConfiguration = "[He] 2s²", electronegativity = 1.57, meltingPointKelvin = 1560.0, boilingPointKelvin = 2742.0,
        density = 1.85, phaseAtSTP = "Solid", discoveryYear = "1798", discoveredBy = "Louis Nicolas Vauquelin",
        summary = "Relatively rare metal in the universe, forms emeralds when present with beryl mineral.",
        shells = listOf(2, 2), oxidationStates = listOf(2)
    ),
    Element(
        atomicNumber = 5, symbol = "B", name = "Boron", atomicWeight = 10.81,
        category = ElementCategory.METALLOID, period = 2, group = 13, block = "p",
        electronConfiguration = "[He] 2s² 2p¹", electronegativity = 2.04, meltingPointKelvin = 2349.0, boilingPointKelvin = 4200.0,
        density = 2.34, phaseAtSTP = "Solid", discoveryYear = "1808", discoveredBy = "Joseph Louis Gay-Lussac & Louis Jacques Thénard",
        summary = "Low-abundance metalloid used in fiberglass, semiconductor doping, and borosilicate glassware.",
        shells = listOf(2, 3), oxidationStates = listOf(3)
    ),
    Element(
        atomicNumber = 6, symbol = "C", name = "Carbon", atomicWeight = 12.011,
        category = ElementCategory.REACTIVE_NONMETAL, period = 2, group = 14, block = "p",
        electronConfiguration = "[He] 2s² 2p²", electronegativity = 2.55, meltingPointKelvin = 3823.0, boilingPointKelvin = 4098.0,
        density = 2.267, phaseAtSTP = "Solid", discoveryYear = "Ancient", discoveredBy = "Known to ancients",
        summary = "Foundation of organic chemistry and all known life. Capable of forming stable covalent bonds with 4 atoms.",
        shells = listOf(2, 4), oxidationStates = listOf(-4, -2, 2, 4)
    ),
    Element(
        atomicNumber = 7, symbol = "N", name = "Nitrogen", atomicWeight = 14.007,
        category = ElementCategory.REACTIVE_NONMETAL, period = 2, group = 15, block = "p",
        electronConfiguration = "[He] 2s² 2p³", electronegativity = 3.04, meltingPointKelvin = 63.15, boilingPointKelvin = 77.36,
        density = 0.0012506, phaseAtSTP = "Gas", discoveryYear = "1772", discoveredBy = "Daniel Rutherford",
        summary = "Constitutes about 78% of Earth's atmosphere. Triple bonded N₂ makes it inert at room temperature.",
        shells = listOf(2, 5), oxidationStates = listOf(-3, 3, 5)
    ),
    Element(
        atomicNumber = 8, symbol = "O", name = "Oxygen", atomicWeight = 15.999,
        category = ElementCategory.REACTIVE_NONMETAL, period = 2, group = 16, block = "p",
        electronConfiguration = "[He] 2s² 2p⁴", electronegativity = 3.44, meltingPointKelvin = 54.36, boilingPointKelvin = 90.20,
        density = 0.001429, phaseAtSTP = "Gas", discoveryYear = "1774", discoveredBy = "Carl Wilhelm Scheele & Joseph Priestley",
        summary = "Highly reactive nonmetal and oxidizing agent. Essential for cellular respiration and combustion.",
        shells = listOf(2, 6), oxidationStates = listOf(-2, -1)
    ),
    Element(
        atomicNumber = 9, symbol = "F", name = "Fluorine", atomicWeight = 18.998,
        category = ElementCategory.HALOGEN, period = 2, group = 17, block = "p",
        electronConfiguration = "[He] 2s² 2p⁵", electronegativity = 3.98, meltingPointKelvin = 53.53, boilingPointKelvin = 85.03,
        density = 0.001696, phaseAtSTP = "Gas", discoveryYear = "1886", discoveredBy = "Henri Moissan",
        summary = "Most electronegative and chemically reactive of all chemical elements. Extremely corrosive.",
        shells = listOf(2, 7), oxidationStates = listOf(-1)
    ),
    Element(
        atomicNumber = 10, symbol = "Ne", name = "Neon", atomicWeight = 20.180,
        category = ElementCategory.NOBLE_GAS, period = 2, group = 18, block = "p",
        electronConfiguration = "[He] 2s² 2p⁶", electronegativity = null, meltingPointKelvin = 24.56, boilingPointKelvin = 27.07,
        density = 0.0009002, phaseAtSTP = "Gas", discoveryYear = "1898", discoveredBy = "Sir William Ramsay & Morris Travers",
        summary = "Colorless, odorless inert gas that glows reddish-orange in high-voltage electrical discharge signs.",
        shells = listOf(2, 8), oxidationStates = emptyList()
    ),
    Element(
        atomicNumber = 11, symbol = "Na", name = "Sodium", atomicWeight = 22.990,
        category = ElementCategory.ALKALI_METAL, period = 3, group = 1, block = "s",
        electronConfiguration = "[Ne] 3s¹", electronegativity = 0.93, meltingPointKelvin = 370.87, boilingPointKelvin = 1156.0,
        density = 0.968, phaseAtSTP = "Solid", discoveryYear = "1807", discoveredBy = "Humphry Davy",
        summary = "Soft, silvery alkali metal that reacts vigorously with water releasing hydrogen gas and forming NaOH.",
        shells = listOf(2, 8, 1), oxidationStates = listOf(1)
    ),
    Element(
        atomicNumber = 12, symbol = "Mg", name = "Magnesium", atomicWeight = 24.305,
        category = ElementCategory.ALKALINE_EARTH, period = 3, group = 2, block = "s",
        electronConfiguration = "[Ne] 3s²", electronegativity = 1.31, meltingPointKelvin = 923.0, boilingPointKelvin = 1363.0,
        density = 1.738, phaseAtSTP = "Solid", discoveryYear = "1755", discoveredBy = "Joseph Black",
        summary = "Shiny gray solid, burns with an intense brilliant white flame. Essential central atom in chlorophyll.",
        shells = listOf(2, 8, 2), oxidationStates = listOf(2)
    ),
    Element(
        atomicNumber = 13, symbol = "Al", name = "Aluminium", atomicWeight = 26.982,
        category = ElementCategory.POST_TRANSITION, period = 3, group = 13, block = "p",
        electronConfiguration = "[Ne] 3s² 3p¹", electronegativity = 1.61, meltingPointKelvin = 933.47, boilingPointKelvin = 2792.0,
        density = 2.70, phaseAtSTP = "Solid", discoveryYear = "1825", discoveredBy = "Hans Christian Ørsted",
        summary = "Lightweight, corrosion-resistant post-transition metal. The most widespread metal in Earth's crust.",
        shells = listOf(2, 8, 3), oxidationStates = listOf(3)
    ),
    Element(
        atomicNumber = 14, symbol = "Si", name = "Silicon", atomicWeight = 28.085,
        category = ElementCategory.METALLOID, period = 3, group = 14, block = "p",
        electronConfiguration = "[Ne] 3s² 3p²", electronegativity = 1.90, meltingPointKelvin = 1687.0, boilingPointKelvin = 3538.0,
        density = 2.329, phaseAtSTP = "Solid", discoveryYear = "1824", discoveredBy = "Jöns Jacob Berzelius",
        summary = "Hard, brittle semiconductor. Foundation of modern electronics, computer microchips, and quartz silica.",
        shells = listOf(2, 8, 4), oxidationStates = listOf(-4, 2, 4)
    ),
    Element(
        atomicNumber = 15, symbol = "P", name = "Phosphorus", atomicWeight = 30.974,
        category = ElementCategory.REACTIVE_NONMETAL, period = 3, group = 15, block = "p",
        electronConfiguration = "[Ne] 3s² 3p³", electronegativity = 2.19, meltingPointKelvin = 317.3, boilingPointKelvin = 553.6,
        density = 1.823, phaseAtSTP = "Solid", discoveryYear = "1669", discoveredBy = "Hennig Brand",
        summary = "Essential for DNA, RNA, and ATP. Exists in white, red, and black allotropes; white phosphorus glows in the dark.",
        shells = listOf(2, 8, 5), oxidationStates = listOf(-3, 3, 5)
    ),
    Element(
        atomicNumber = 16, symbol = "S", name = "Sulfur", atomicWeight = 32.06,
        category = ElementCategory.REACTIVE_NONMETAL, period = 3, group = 16, block = "p",
        electronConfiguration = "[Ne] 3s² 3p⁴", electronegativity = 2.58, meltingPointKelvin = 388.36, boilingPointKelvin = 717.8,
        density = 2.07, phaseAtSTP = "Solid", discoveryYear = "Ancient", discoveredBy = "Known to ancients",
        summary = "Bright yellow crystalline solid. Used in sulfuric acid production (H₂SO₄), gunpowder, and vulcanization.",
        shells = listOf(2, 8, 6), oxidationStates = listOf(-2, 2, 4, 6)
    ),
    Element(
        atomicNumber = 17, symbol = "Cl", name = "Chlorine", atomicWeight = 35.45,
        category = ElementCategory.HALOGEN, period = 3, group = 17, block = "p",
        electronConfiguration = "[Ne] 3s² 3p⁵", electronegativity = 3.16, meltingPointKelvin = 171.6, boilingPointKelvin = 239.11,
        density = 0.0032, phaseAtSTP = "Gas", discoveryYear = "1774", discoveredBy = "Carl Wilhelm Scheele",
        summary = "Yellow-green toxic halogen gas. Widely used in water disinfection and as table salt (NaCl).",
        shells = listOf(2, 8, 7), oxidationStates = listOf(-1, 1, 3, 5, 7)
    ),
    Element(
        atomicNumber = 18, symbol = "Ar", name = "Argon", atomicWeight = 39.948,
        category = ElementCategory.NOBLE_GAS, period = 3, group = 18, block = "p",
        electronConfiguration = "[Ne] 3s² 3p⁶", electronegativity = null, meltingPointKelvin = 83.80, boilingPointKelvin = 87.30,
        density = 0.001784, phaseAtSTP = "Gas", discoveryYear = "1894", discoveredBy = "Lord Rayleigh & William Ramsay",
        summary = "Third most abundant gas in Earth's atmosphere (0.934%). Used as shielding gas in arc welding.",
        shells = listOf(2, 8, 8), oxidationStates = emptyList()
    ),
    Element(
        atomicNumber = 19, symbol = "K", name = "Potassium", atomicWeight = 39.098,
        category = ElementCategory.ALKALI_METAL, period = 4, group = 1, block = "s",
        electronConfiguration = "[Ar] 4s¹", electronegativity = 0.82, meltingPointKelvin = 336.53, boilingPointKelvin = 1032.0,
        density = 0.862, phaseAtSTP = "Solid", discoveryYear = "1807", discoveredBy = "Humphry Davy",
        summary = "Soft silvery alkali metal. Vital for nervous system function, muscle contraction, and plant growth.",
        shells = listOf(2, 8, 8, 1), oxidationStates = listOf(1)
    ),
    Element(
        atomicNumber = 20, symbol = "Ca", name = "Calcium", atomicWeight = 40.078,
        category = ElementCategory.ALKALINE_EARTH, period = 4, group = 2, block = "s",
        electronConfiguration = "[Ar] 4s²", electronegativity = 1.00, meltingPointKelvin = 1115.0, boilingPointKelvin = 1757.0,
        density = 1.54, phaseAtSTP = "Solid", discoveryYear = "1808", discoveredBy = "Humphry Davy",
        summary = "Most abundant mineral in human body; essential for bones, teeth, and cellular signaling.",
        shells = listOf(2, 8, 8, 2), oxidationStates = listOf(2)
    ),
    Element(
        atomicNumber = 21, symbol = "Sc", name = "Scandium", atomicWeight = 44.956,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 3, block = "d",
        electronConfiguration = "[Ar] 3d¹ 4s²", electronegativity = 1.36, meltingPointKelvin = 1814.0, boilingPointKelvin = 3109.0,
        density = 2.985, phaseAtSTP = "Solid", discoveryYear = "1879", discoveredBy = "Lars Fredrik Nilson",
        summary = "Silvery-white metallic transition metal. Used in high-performance aerospace aluminum alloys.",
        shells = listOf(2, 8, 9, 2), oxidationStates = listOf(3)
    ),
    Element(
        atomicNumber = 22, symbol = "Ti", name = "Titanium", atomicWeight = 47.867,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 4, block = "d",
        electronConfiguration = "[Ar] 3d² 4s²", electronegativity = 1.54, meltingPointKelvin = 1941.0, boilingPointKelvin = 3560.0,
        density = 4.506, phaseAtSTP = "Solid", discoveryYear = "1791", discoveredBy = "William Gregor",
        summary = "Renowned for high strength-to-weight ratio and corrosion resistance. Used in medical implants and jet engines.",
        shells = listOf(2, 8, 10, 2), oxidationStates = listOf(2, 3, 4)
    ),
    Element(
        atomicNumber = 23, symbol = "V", name = "Vanadium", atomicWeight = 50.942,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 5, block = "d",
        electronConfiguration = "[Ar] 3d³ 4s²", electronegativity = 1.63, meltingPointKelvin = 2183.0, boilingPointKelvin = 3680.0,
        density = 6.11, phaseAtSTP = "Solid", discoveryYear = "1801", discoveredBy = "Andrés Manuel del Río",
        summary = "Hard, silvery-grey, ductile transition metal. Used to produce high-strength shock-resistant steel tools.",
        shells = listOf(2, 8, 11, 2), oxidationStates = listOf(2, 3, 4, 5)
    ),
    Element(
        atomicNumber = 24, symbol = "Cr", name = "Chromium", atomicWeight = 51.996,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 6, block = "d",
        electronConfiguration = "[Ar] 3d⁵ 4s¹", electronegativity = 1.66, meltingPointKelvin = 2180.0, boilingPointKelvin = 2944.0,
        density = 7.19, phaseAtSTP = "Solid", discoveryYear = "1797", discoveredBy = "Louis Nicolas Vauquelin",
        summary = "Hard, lustrous metal that takes a high polish. Primary alloying element in stainless steel.",
        shells = listOf(2, 8, 13, 1), oxidationStates = listOf(2, 3, 6)
    ),
    Element(
        atomicNumber = 25, symbol = "Mn", name = "Manganese", atomicWeight = 54.938,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 7, block = "d",
        electronConfiguration = "[Ar] 3d⁵ 4s²", electronegativity = 1.55, meltingPointKelvin = 1519.0, boilingPointKelvin = 2334.0,
        density = 7.21, phaseAtSTP = "Solid", discoveryYear = "1774", discoveredBy = "Johan Gottlieb Gahn",
        summary = "Essential in steelmaking to prevent brittleness; potassium permanganate (KMnO₄) is a prominent laboratory oxidizer.",
        shells = listOf(2, 8, 13, 2), oxidationStates = listOf(2, 3, 4, 7)
    ),
    Element(
        atomicNumber = 26, symbol = "Fe", name = "Iron", atomicWeight = 55.845,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 8, block = "d",
        electronConfiguration = "[Ar] 3d⁶ 4s²", electronegativity = 1.83, meltingPointKelvin = 1811.0, boilingPointKelvin = 3134.0,
        density = 7.874, phaseAtSTP = "Solid", discoveryYear = "Ancient", discoveredBy = "Known to ancients",
        summary = "Most used metal by mass, foundation of civilization and steel. Carries oxygen in blood hemoglobin.",
        shells = listOf(2, 8, 14, 2), oxidationStates = listOf(2, 3)
    ),
    Element(
        atomicNumber = 27, symbol = "Co", name = "Cobalt", atomicWeight = 58.933,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 9, block = "d",
        electronConfiguration = "[Ar] 3d⁷ 4s²", electronegativity = 1.88, meltingPointKelvin = 1768.0, boilingPointKelvin = 3200.0,
        density = 8.90, phaseAtSTP = "Solid", discoveryYear = "1735", discoveredBy = "Georg Brandt",
        summary = "Hard ferromagnetic metal with deep blue pigments. Crucial component of lithium-ion rechargeable batteries.",
        shells = listOf(2, 8, 15, 2), oxidationStates = listOf(2, 3)
    ),
    Element(
        atomicNumber = 28, symbol = "Ni", name = "Nickel", atomicWeight = 58.693,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 10, block = "d",
        electronConfiguration = "[Ar] 3d⁸ 4s²", electronegativity = 1.91, meltingPointKelvin = 1728.0, boilingPointKelvin = 3186.0,
        density = 8.908, phaseAtSTP = "Solid", discoveryYear = "1751", discoveredBy = "Axel Fredrik Cronstedt",
        summary = "Silvery-white lustrous transition metal with a slight golden tinge. Widely used in coins and stainless steel.",
        shells = listOf(2, 8, 16, 2), oxidationStates = listOf(2, 3)
    ),
    Element(
        atomicNumber = 29, symbol = "Cu", name = "Copper", atomicWeight = 63.546,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 11, block = "d",
        electronConfiguration = "[Ar] 3d¹⁰ 4s¹", electronegativity = 1.90, meltingPointKelvin = 1357.77, boilingPointKelvin = 2835.0,
        density = 8.96, phaseAtSTP = "Solid", discoveryYear = "Ancient", discoveredBy = "Known to ancients",
        summary = "Soft, malleable, ductile metal with exceptional electrical and thermal conductivity. Forms green patina over time.",
        shells = listOf(2, 8, 18, 1), oxidationStates = listOf(1, 2)
    ),
    Element(
        atomicNumber = 30, symbol = "Zn", name = "Zinc", atomicWeight = 65.38,
        category = ElementCategory.TRANSITION_METAL, period = 4, group = 12, block = "d",
        electronConfiguration = "[Ar] 3d¹⁰ 4s²", electronegativity = 1.65, meltingPointKelvin = 692.68, boilingPointKelvin = 1180.0,
        density = 7.14, phaseAtSTP = "Solid", discoveryYear = "1746", discoveredBy = "Andreas Sigismund Marggraf",
        summary = "Bluish-white metal used to galvanize steel against rusting and as anode in alkaline batteries.",
        shells = listOf(2, 8, 18, 2), oxidationStates = listOf(2)
    ),
    Element(
        atomicNumber = 31, symbol = "Ga", name = "Gallium", atomicWeight = 69.723,
        category = ElementCategory.POST_TRANSITION, period = 4, group = 13, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p¹", electronegativity = 1.81, meltingPointKelvin = 302.91, boilingPointKelvin = 2673.0,
        density = 5.91, phaseAtSTP = "Solid", discoveryYear = "1875", discoveredBy = "Paul-Émile Lecoq de Boisbaudran",
        summary = "Soft silvery metal that melts in human hand (mp ~29.7°C). Core material for semiconductor laser diodes.",
        shells = listOf(2, 8, 18, 3), oxidationStates = listOf(3)
    ),
    Element(
        atomicNumber = 32, symbol = "Ge", name = "Germanium", atomicWeight = 72.630,
        category = ElementCategory.METALLOID, period = 4, group = 14, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p²", electronegativity = 2.01, meltingPointKelvin = 1211.40, boilingPointKelvin = 3106.0,
        density = 5.323, phaseAtSTP = "Solid", discoveryYear = "1886", discoveredBy = "Clemens Winkler",
        summary = "Lustrous, hard metalloid predicted as 'eka-silicon' by Mendeleev, key to fiber optics and infrared optics.",
        shells = listOf(2, 8, 18, 4), oxidationStates = listOf(2, 4)
    ),
    Element(
        atomicNumber = 33, symbol = "As", name = "Arsenic", atomicWeight = 74.922,
        category = ElementCategory.METALLOID, period = 4, group = 15, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p³", electronegativity = 2.18, meltingPointKelvin = 1090.0, boilingPointKelvin = 887.0,
        density = 5.727, phaseAtSTP = "Solid", discoveryYear = "1250", discoveredBy = "Albertus Magnus",
        summary = "Famous metalloid, historical poison, but also utilized in GaAs high-frequency semiconductor chips.",
        shells = listOf(2, 8, 18, 5), oxidationStates = listOf(-3, 3, 5)
    ),
    Element(
        atomicNumber = 34, symbol = "Se", name = "Selenium", atomicWeight = 78.971,
        category = ElementCategory.REACTIVE_NONMETAL, period = 4, group = 16, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p⁴", electronegativity = 2.55, meltingPointKelvin = 494.0, boilingPointKelvin = 958.0,
        density = 4.81, phaseAtSTP = "Solid", discoveryYear = "1817", discoveredBy = "Jöns Jacob Berzelius",
        summary = "Photoconductive nonmetal whose electrical conductivity increases when illuminated with light.",
        shells = listOf(2, 8, 18, 6), oxidationStates = listOf(-2, 4, 6)
    ),
    Element(
        atomicNumber = 35, symbol = "Br", name = "Bromine", atomicWeight = 79.904,
        category = ElementCategory.HALOGEN, period = 4, group = 17, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p⁵", electronegativity = 2.96, meltingPointKelvin = 265.8, boilingPointKelvin = 332.0,
        density = 3.1028, phaseAtSTP = "Liquid", discoveryYear = "1826", discoveredBy = "Antoine Jérôme Balard",
        summary = "Fuming deep red-brown liquid halogen at room temperature, one of only two liquid elements at standard conditions.",
        shells = listOf(2, 8, 18, 7), oxidationStates = listOf(-1, 1, 5)
    ),
    Element(
        atomicNumber = 36, symbol = "Kr", name = "Krypton", atomicWeight = 83.798,
        category = ElementCategory.NOBLE_GAS, period = 4, group = 18, block = "p",
        electronConfiguration = "[Ar] 3d¹⁰ 4s² 4p⁶", electronegativity = 3.00, meltingPointKelvin = 115.79, boilingPointKelvin = 119.93,
        density = 0.003749, phaseAtSTP = "Gas", discoveryYear = "1898", discoveredBy = "William Ramsay & Morris Travers",
        summary = "Noble gas with brilliant white-green spectral emission, historically used to define the international meter.",
        shells = listOf(2, 8, 18, 8), oxidationStates = listOf(2)
    )
)
