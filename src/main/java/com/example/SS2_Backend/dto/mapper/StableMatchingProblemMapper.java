package com.example.SS2_Backend.dto.mapper;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.StableMatchingOTMProblem;
import com.example.SS2_Backend.model.stableMatching.StableMatchingOTOProblem;
import com.example.SS2_Backend.model.stableMatching.StableMatchingProblem;
import com.example.SS2_Backend.ss.smt.MatchingData;
import com.example.SS2_Backend.ss.smt.evaluator.FitnessEvaluator;
import com.example.SS2_Backend.ss.smt.evaluator.impl.TwoSetFitnessEvaluator;
import com.example.SS2_Backend.ss.smt.implement.MTMProblem;
import com.example.SS2_Backend.ss.smt.implement.OTMProblem;
import com.example.SS2_Backend.ss.smt.implement.OTOProblem;
import com.example.SS2_Backend.ss.smt.preference.PreferenceBuilder;
import com.example.SS2_Backend.ss.smt.preference.PreferenceListWrapper;
import com.example.SS2_Backend.ss.smt.preference.impl.provider.TwoSetPreferenceProvider;
import com.example.SS2_Backend.ss.smt.requirement.Requirement;
import com.example.SS2_Backend.ss.smt.requirement.RequirementDecoder;
import com.example.SS2_Backend.util.EvaluatorUtils;

/**
 * Mapper layer, xử lý các công việc sau đối với từng loại matching problem:
 * 1. map problem data từ dto vào StableMatchingProblem
 * 2. tính toán các preference list và set vào StableMatchingProblem
 */
public class StableMatchingProblemMapper {

    public static OTOProblem toOTO(NewStableMatchingProblemDTO dto) {
        //TODO: implement OTO map logic
        return null;
    }


    public static OTMProblem toOTM(NewStableMatchingProblemDTO request) {
        Requirement[][] requirements = RequirementDecoder.decode(request.getIndividualRequirements());
        MatchingData data = new MatchingData(request.getNumberOfIndividuals(),
                request.getNumberOfProperty(),
                request.getIndividualSetIndices(),
                request.getIndividualCapacities(),
                request.getIndividualProperties(),
                request.getIndividualWeights(),
                requirements);
        data.setExcludedPairs(request.getExcludedPairs());
        PreferenceBuilder builder = new TwoSetPreferenceProvider(data,
                request.getEvaluateFunctions());
        PreferenceListWrapper preferenceLists = builder.toListWrapper();
        FitnessEvaluator fitnessEvaluator = new TwoSetFitnessEvaluator(data);
        return new OTMProblem(request.getProblemName(),
                request.getNumberOfIndividuals(),
                request.getNumberOfSets(),
                data,
                preferenceLists,
                request.getFitnessFunction(),
                fitnessEvaluator);
    }

    public static MTMProblem toMTM(NewStableMatchingProblemDTO request) {
        Requirement[][] requirements = RequirementDecoder.decode(request.getIndividualRequirements());
        MatchingData data = new MatchingData(request.getNumberOfIndividuals(),
                request.getNumberOfProperty(),
                request.getIndividualSetIndices(),
                request.getIndividualCapacities(),
                request.getIndividualProperties(),
                request.getIndividualWeights(),
                requirements);
        data.setExcludedPairs(request.getExcludedPairs());
        PreferenceBuilder builder = new TwoSetPreferenceProvider(data,
                request.getEvaluateFunctions());
        PreferenceListWrapper preferenceLists = builder.toListWrapper();
        FitnessEvaluator fitnessEvaluator = new TwoSetFitnessEvaluator(data);
        String fitnessFunction = EvaluatorUtils.getValidFitnessFunction(request.getFitnessFunction());
        return new MTMProblem(request.getProblemName(),
                request.getNumberOfIndividuals(),
                request.getNumberOfSets(),
                data,
                preferenceLists,
                fitnessFunction,
                fitnessEvaluator);
    }

}
