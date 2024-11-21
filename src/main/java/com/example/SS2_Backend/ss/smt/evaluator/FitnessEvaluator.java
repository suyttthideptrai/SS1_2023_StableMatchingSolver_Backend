package com.example.SS2_Backend.ss.smt.evaluator;

import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.match.Matches;

import java.util.List;

/**
 * FitnessEvaluator in tơ phịt
 */
public interface FitnessEvaluator {

    /**
     * Tôi nghĩ hàm này nên được lấy bên PreferenceList thì hay hơn.
     * Thằng FitnessEvaluator chỉ cần nhận array giá trị và phệt logic vào thôi.
     */
//    double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists);

    double defaultFitnessEvaluation(double[] satisfactions);

    double withFitnessFunctionEvaluation(double[] satisfactions, String fnf);

}
