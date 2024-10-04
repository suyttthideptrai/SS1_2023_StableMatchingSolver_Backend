package com.example.SS2_Backend.model.StableMatching.Solutions;

import com.example.SS2_Backend.dto.response.ComputerSpecs;
import com.example.SS2_Backend.model.StableMatching.Matches.MatchesOTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OTOSolution {
    private MatchesOTO matches;
    private double fitnessValue;
    private double runtime;
    private ComputerSpecs computerSpecs;
    private String algorithm;
    private double[] setSatisfactions;
}
