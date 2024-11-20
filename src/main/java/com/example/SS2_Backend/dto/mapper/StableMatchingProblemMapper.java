package com.example.SS2_Backend.dto.mapper;

import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.model.stableMatching.StableMatchingOTMProblem;
import com.example.SS2_Backend.model.stableMatching.StableMatchingOTOProblem;
import com.example.SS2_Backend.model.stableMatching.StableMatchingProblem;

/**
 * Mapper layer, xử lý các công việc sau đối với từng loại matching problem:
 * 1. map problem data từ dto vào StableMatchingProblem
 * 2. tính toán các preference list và set vào StableMatchingProblem
 */
public class StableMatchingProblemMapper {

    public StableMatchingOTOProblem toOTO(StableMatchingProblemDTO dto) {
        StableMatchingOTOProblem problem = new StableMatchingOTOProblem();
        //TODO: implement OTO map logic
        return problem;
    }


    public StableMatchingOTMProblem toOTM(StableMatchingOTMProblem dto) {
        StableMatchingOTMProblem problem = new StableMatchingOTMProblem();
        //TODO: implement OTM map logic
        return problem;
    }

    public StableMatchingProblem toMTM(StableMatchingProblemDTO dto) {
        StableMatchingProblem problem = new StableMatchingProblem();
        //TODO: implement MTM map logic
        return problem;
    }

}
