package com.example.SS2_Backend.ss.smt;

import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.ss.smt.preference.PreferenceBuilder;
import com.example.SS2_Backend.ss.smt.preference.impl.list.OldPreferenceList;

import java.util.List;

// Khả năng viết thành class luôn ko cần interface
public interface MatchingProblemBuilder {
    void setPopulation(NewStableMatchingProblemDTO request);
    PreferenceBuilder createPreferencesProvider();
    List<OldPreferenceList> getPreferences();
    PreferenceBuilder getPreferencesProvider();
    void setPreferencesProvider(PreferenceBuilder provider);

    void setPreferenceLists(List<OldPreferenceList> oldPreferenceListImpls);
    String getEvaluateFunctionForSet1();
    String getEvaluateFunctionForSet2();
    int[] getIndividualSetIndices();
}
