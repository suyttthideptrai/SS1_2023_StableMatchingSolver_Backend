package com.example.SS2_Backend.ss.smt.evaluator;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.match.Matches;

import java.util.List;

/**
 * TA: hmm nếu các problem stable matching không có gì thay đổi về logic evaluate fitness thì chắc chả cần Solid
 * 1. Bỏ hẳn solid đi
 * 2. Impl một cái StandardFitnessEvaluatorImpl rồi tạm thời dùng chung hết (chắc theo hướng này :v)
 */
public interface FitnessEvaluator {

    double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists);

    double defaultFitnessEvaluation(double[] satisfactions);

    double withFitnessFunctionEvaluation(double[] satisfactions, String fnf);

    String getFitnessFunction();

}
