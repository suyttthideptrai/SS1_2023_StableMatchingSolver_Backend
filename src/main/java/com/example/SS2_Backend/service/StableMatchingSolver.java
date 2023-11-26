package com.example.SS2_Backend.service;

import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.dto.response.Progress;
import com.example.SS2_Backend.dto.response.Response;
import com.example.SS2_Backend.model.GameSolutionInsights;
import com.example.SS2_Backend.model.StableMatching.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class StableMatchingSolver {
//    SimpMessageSendingOperations simpMessagingTemplate;
//    private static final int RUN_COUNT_PER_ALGORITHM = 10; // for insight running, each algorithm will be run for 10 times


    public static ResponseEntity<Response> solveStableMatching(StableMatchingProblemDTO request) {

        try{
            StableMatchingProblem problem = new StableMatchingProblem();

            problem.setPopulation(request.getIndividuals());
            problem.setAllPropertyNames(request.getAllPropertyNames());
            problem.setFitnessFunction(request.getFitnessFunction());

            System.out.println(problem.printPreferenceLists());

            long startTime = System.currentTimeMillis();

            NondominatedPopulation results = solveProblem(
                    problem,
    //                request.getSpecifiedAlgorithm(),
                    request.getPopulationSize(),
                    request.getFitnessFunction(),
                    request.getEvolutionRate(),
                    request.getMaximumExecutionTime()
            );

            long endTime = System.currentTimeMillis();
            double runtime = ((double) (endTime - startTime) / 1000 / 60);
            runtime = Math.round(runtime * 100.0) / 100.0;
            System.out.println("Runtime: " + runtime + " Second(s).");
            MatchingSolution matchingSolution = formatSolution(problem, results, runtime);
            System.out.println("RESPOND TO FRONT_END:");
            System.out.println(matchingSolution);
            return ResponseEntity.ok(
                    Response.builder()
                            .status(200)
                            .message("Solve stable matching problem successfully!")
                            .data(matchingSolution)
                            .build()
            );
        } catch (Exception e) {
            // Handle exceptions and return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error solving stable matching problem.")
                            .data(null)
                            .build());
        }
    }
    private static MatchingSolution formatSolution(StableMatchingProblem problem, NondominatedPopulation result, double Runtime){
        Solution solution = result.get(0);
        MatchingSolution matchingSolution = new MatchingSolution();
        double fitnessValue = solution.getObjective(0);
        Matches matches = (Matches) solution.getAttribute("matches");


        matchingSolution.setFitnessValue(-fitnessValue);
        matchingSolution.setMatches(matches);
        matchingSolution.setAlgorithm("NSGAII");
        matchingSolution.setRuntime(Runtime);

        return matchingSolution;
    }


    public static String convertObjectToJson(Object object) {
        try {
            // Create an instance of ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            StableMatchingProblemDTO stableMatchingProblem = objectMapper.convertValue(object, StableMatchingProblemDTO.class);

            return objectMapper.writeValueAsString(stableMatchingProblem);
        } catch (Exception e) {
            // Handle exception if needed
            e.printStackTrace();
            return "Error converting object to JSON";
        }
    }



    private static NondominatedPopulation solveProblem(StableMatchingProblem problem,
                                                       int populationSize,
                                                       String fitnessFunction,
                                                       int evolutionRate,
                                                       int maximumExecutionTime) {
        NondominatedPopulation result = new Executor()
                .withProblem(problem)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(1000)
                .withProperty("populationSize", 200)
                .distributeOnAllCores()
                .run();
        for (Solution solution : result) {
            System.out.println("Randomized Individuals Input Order (by MOEA): " + solution.getVariable(0).toString());
            Matches matches = (Matches) solution.getAttribute("matches");
            System.out.println("Output Matches (by Gale Shapley):\n" + matches.toString());
            System.out.println("Fitness Score: " + -solution.getObjective(0));
        }
        return result;
    }



//    public ResponseEntity<Response> getProblemResultInsights(StableMatchingProblemDTO request, String sessionCode) {
////        log.info("Received request: " + request);
//        String[] algorithms = {"NSGAII", "NSGAIII", "eMOEA", "PESA2", "VEGA"};
//
//
//
//        simpMessagingTemplate.convertAndSendToUser(sessionCode, "/progress", createProgressMessage("Initializing the problem..."));
//        StableMatchingProblem problem = new StableMatchingProblem(new ArrayList<Individual>(), "BCA", "ABC");
//
//        problem.setCompositeWeightFunction(request.getCompositeWeightFunction());
//        problem.setFitnessFunction(request.getFitnessFunction());
//        problem.setSpecifiedAlgorithm(request.getSpecifiedAlgorithm());
//
//        problem.setPopulationSize(request.getPopulationSize());
//        problem.setEvolutionRate(request.getEvolutionRate());
//        problem.setMaximumExecutionTime(request.getMaximumExecutionTime());
//
//        GameSolutionInsights gameSolutionInsights = initGameSolutionInsights(algorithms);
//
//        int runCount = 1;
//        int maxRunCount = algorithms.length * RUN_COUNT_PER_ALGORITHM;
//        // solve the problem with different algorithms and then evaluate the performance of the algorithms
////        log.info("Start benchmarking the algorithms...");
//        simpMessagingTemplate.convertAndSendToUser(sessionCode, "/progress", createProgressMessage("Start benchmarking the algorithms..."));
//
//        for (String algorithm : algorithms) {
////            log.info("Running algorithm: " + algorithm + "...");
//            for (int i = 0; i < RUN_COUNT_PER_ALGORITHM; i++) {
//                System.out.println("Iteration: " + i);
//                long start = System.currentTimeMillis();
//
//                NondominatedPopulation results = solveProblem(
//                        problem,
//                        request.getSpecifiedAlgorithm(),
//                        request.getPopulationSize(),
//                        request.getFitnessFunction(),
//
//                        request.getEvolutionRate(),
//                        request.getCompositeWeightFunction(),
//                        request.getMaximumExecutionTime()
//                );
//
//                long end = System.currentTimeMillis();
//
//                double runtime = (double) (end - start) / 1000;
//                double fitnessValue = getFitnessValue(results);
//
//                // send the progress to the client
//                String message = "Algorithm " + algorithm + " finished iteration: #" + (i + 1) + "/" + RUN_COUNT_PER_ALGORITHM;
//                Progress progress = createProgress(message, runtime, runCount, maxRunCount);
//                System.out.println(progress);
//                simpMessagingTemplate.convertAndSendToUser(sessionCode, "/progress", progress);
//                runCount++;
//
//                // add the fitness value and runtime to the insights
//                gameSolutionInsights.getFitnessValues().get(algorithm).add(fitnessValue);
//                gameSolutionInsights.getRuntimes().get(algorithm).add(runtime);
//
//
//            }
//
//        }
////        log.info("Benchmarking finished!");
//        simpMessagingTemplate.convertAndSendToUser(sessionCode, "/progress", createProgressMessage("Benchmarking finished!"));
//
//        return ResponseEntity.ok(
//                Response.builder()
//                        .status(200)
//                        .message("Get problem result insights successfully!")
//                        .data(gameSolutionInsights)
//                        .build()
//        );
//    }

    private GameSolutionInsights initGameSolutionInsights(String[] algorithms) {
        GameSolutionInsights gameSolutionInsights = new GameSolutionInsights();
        Map<String, List<Double>> fitnessValueMap = new HashMap<>();
        Map<String, List<Double>> runtimeMap = new HashMap<>();

        gameSolutionInsights.setFitnessValues(fitnessValueMap);
        gameSolutionInsights.setRuntimes(runtimeMap);

        for (String algorithm : algorithms) {
            fitnessValueMap.put(algorithm, new ArrayList<>());
            runtimeMap.put(algorithm, new ArrayList<>());
        }

        return gameSolutionInsights;
    }

    private Progress createProgressMessage(String message) {
        return Progress.builder()
                .inProgress(false) // this object is just to send a message to the client, not to show the progress
                .message(message)
                .build();
    }

    private Progress createProgress(String message, Double runtime, Integer runCount, int maxRunCount) {
        int percent = runCount * 100 / maxRunCount;
        int minuteLeff = (int) Math.ceil(((maxRunCount - runCount) * runtime) / 60); // runtime is in seconds
        return Progress.builder()
                .inProgress(true) // this object is just to send to the client to show the progress
                .message(message)
                .runtime(runtime)
                .minuteLeft(minuteLeff)
                .percentage(percent)
                .build();
    }

    private static double getFitnessValue(NondominatedPopulation result) {

        Solution solution = result.get(0);
        double fitnessValue = solution.getObjective(0);
        return fitnessValue;

    }


}
