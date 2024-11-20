package com.example.SS2_Backend.ss.smt;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.ss.smt.preference.impl.list.OldPreferenceList;
import com.example.SS2_Backend.ss.smt.preference.PreferenceProvider;

import java.util.List;

// Khả năng viết thành class luôn ko cần interface
public interface MatchingProblemBuilder {
    void setPopulation(NewStableMatchingProblemDTO request);
    PreferenceProvider createPreferencesProvider();
    List<OldPreferenceList> getPreferences();
    PreferenceProvider getPreferencesProvider();
    void setPreferencesProvider(PreferenceProvider provider);

    void setPreferenceLists(List<OldPreferenceList> oldPreferenceListImpls);
    String getEvaluateFunctionForSet1();
    String getEvaluateFunctionForSet2();
    int[] getIndividualSetIndices();
}
