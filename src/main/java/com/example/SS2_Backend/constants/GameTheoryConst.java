package com.example.SS2_Backend.constants;

import java.util.Set;

public class GameTheoryConst {


    public static final String[] ALLOWED_INSIGHT_ALGORITHMS = {
            "NSGAII",
            "NSGAIII",
            "eMOEA",
            "PESA2",
            "VEGA",
            "OMOPSO",
            "SMPSO"};
    public static final Set<String> PAYOFF_VARIABLE_PREFIXES = Set.of("p");
    public static final Set<String> FITNESS_VARIABLE_PREFIXES = Set.of("u");

}
