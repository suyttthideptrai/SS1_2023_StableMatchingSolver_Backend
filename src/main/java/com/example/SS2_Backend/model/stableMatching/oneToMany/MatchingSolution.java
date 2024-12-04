package com.example.SS2_Backend.model.stableMatching.oneToMany;

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

    private Matches matches;
    private double fitnessValue;
    private double runtime;
    private ComputerSpecs computerSpecs;
    private String algorithm;
    //private List<OldPreferenceListImpl> Preferences;
    //private List<Individual> individuals;
    private double[] setSatisfactions;

}
