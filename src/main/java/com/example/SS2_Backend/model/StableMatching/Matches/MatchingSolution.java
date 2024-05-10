package com.example.SS2_Backend.model.StableMatching.Matches;

import com.example.SS2_Backend.dto.request.IndividualDeserializer;
import com.example.SS2_Backend.dto.response.ComputerSpecs;
import com.example.SS2_Backend.model.StableMatching.Individual;
import com.example.SS2_Backend.model.StableMatching.PreferenceList;
import com.example.SS2_Backend.util.ComputerSpecsUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<PreferenceList> Preferences;
    private List<Individual> individuals;
    private double[] setSatisfactions;
    public ComputerSpecs getComputerSpecs() {
        return ComputerSpecsUtil.getComputerSpecs();
    }
    @JsonDeserialize(contentUsing = IndividualDeserializer.class)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pair {
        private String Individual1Name;
        private String Individual2Name;
        private double PairScore;
    }
}
