package com.example.SS2_Backend.ss.smt.util;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.ss.smt.preference.PreferenceList;
import com.example.SS2_Backend.ss.smt.preference.PreferenceProvider;

import java.util.List;

public interface ProblemSetUpUtils {
    void setPopulation(NewStableMatchingProblemDTO request);
    PreferenceProvider createPreferencesProvider();
    List<PreferenceList> getPreferences();
    PreferenceProvider getPreferencesProvider();
    void setPreferencesProvider(PreferenceProvider provider);

    void setPreferenceLists(List<PreferenceList> preferenceLists);
    String getEvaluateFunctionForSet1();
    String getEvaluateFunctionForSet2();
    int[] getIndividualSetIndices();
}
