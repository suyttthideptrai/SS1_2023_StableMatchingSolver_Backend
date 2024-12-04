package com.example.SS2_Backend.ss.smt.evaluator;

/**
 * FitnessEvaluator in tơ phịt
 */
public interface FitnessEvaluator {

    /**
     * TODO: Tôi nghĩ hàm này nên được lấy bên PreferenceList thì hay hơn.
     *  Thằng FitnessEvaluator chỉ cần nhận array giá trị và phệt logic vào thôi.
     */
//    double[] getAllSatisfactions(Matches matches, List<PreferenceList> preferenceLists);

    double defaultFitnessEvaluation(double[] satisfactions);

    double withFitnessFunctionEvaluation(double[] satisfactions, String fnf);

}
