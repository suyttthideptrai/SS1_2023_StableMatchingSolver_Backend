package com.example.SS2_Backend.model;

import com.example.SS2_Backend.dto.mapper.StableMatchingProblemMapper;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.service.StableMatchingSolverRBO;
import com.example.SS2_Backend.ss.smt.MatchingProblem;
import org.junit.jupiter.api.BeforeEach;
import org.moeaframework.core.NondominatedPopulation;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Method;

public class StableMatchingRBORunningTest {
    NewStableMatchingProblemDTO request;

    @BeforeEach
    public void setUp() {
        MatchingProblem problem = StableMatchingProblemMapper.toMTM(request);

        Method method = StableMatchingSolverRBO.class.getDeclaredMethod("solveProblem", NondominatedPopulation.class);
        method.setAccessible(true);

        double actualFitness = (double) method.invoke(new StableMatchingSolverRBO(SimpMessagingTemplate.), Object[]{problem, request.getAlgorithm(),
                request.getPopulationSize(),
                request.getGeneration(),
                request.getMaxTime(),
                request.getDistributedCores()});

//        Object[] args = ;
    }


}
