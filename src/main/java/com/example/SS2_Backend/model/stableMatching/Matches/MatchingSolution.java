package com.example.SS2_Backend.model.stableMatching.Matches;

import com.example.SS2_Backend.dto.response.ComputerSpecs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchingSolution {

    private Object matches;
    private double fitnessValue;
    private double runtime;
    private String algorithm;
    private double[] setSatisfactions;
    private ComputerSpecs computerSpecs;

}
