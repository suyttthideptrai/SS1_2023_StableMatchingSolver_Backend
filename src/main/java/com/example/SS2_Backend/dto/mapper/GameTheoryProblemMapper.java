package com.example.SS2_Backend.dto.mapper;

import com.example.SS2_Backend.dto.request.GameTheoryProblemDTO;
import com.example.SS2_Backend.ss.gt.GameTheoryProblem;
import com.example.SS2_Backend.ss.gt.implement.PSOCompatibleGameTheoryProblem;
import com.example.SS2_Backend.ss.gt.implement.StandardGameTheoryProblem;
import com.example.SS2_Backend.util.EvaluatorUtils;
import com.example.SS2_Backend.util.StringUtils;

import java.util.List;

public class GameTheoryProblemMapper {

    public static GameTheoryProblem toProblem(GameTheoryProblemDTO request) {
        GameTheoryProblem problem;
        String algorithm = request.getAlgorithm();
        if (!StringUtils.isEmptyOrNull(algorithm)
                && List.of("OMOPSO", "SMPSO").contains(algorithm)) {
            problem = new PSOCompatibleGameTheoryProblem();
        } else {
            problem = new StandardGameTheoryProblem();
        }
        problem.setDefaultPayoffFunction(EvaluatorUtils
                .getIfDefaultFunction(request.getDefaultPayoffFunction()));
        problem.setFitnessFunction(EvaluatorUtils
                .getValidFitnessFunction(request.getFitnessFunction()));
        problem.setSpecialPlayer(request.getSpecialPlayer());
        problem.setNormalPlayers(request.getNormalPlayers());
        problem.setConflictSet(request.getConflictSet());
        problem.setMaximizing(request.isMaximizing());

        return problem;
    }
}
