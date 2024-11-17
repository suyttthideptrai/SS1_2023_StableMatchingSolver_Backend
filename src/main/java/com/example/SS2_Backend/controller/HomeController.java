package com.example.SS2_Backend.controller;

import com.example.SS2_Backend.dto.request.GameTheoryProblemDTO;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.dto.request.StableMatchingOTMProblemDTO;
import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.dto.response.Response;
import com.example.SS2_Backend.util.ValidationUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.SS2_Backend.service.GameTheorySolver;
import com.example.SS2_Backend.service.OTMStableMatchingSolver;
import com.example.SS2_Backend.service.StableMatchingSolver;
import com.example.SS2_Backend.service.StableMatchingSolverRBO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private GameTheorySolver gameTheorySolver;
    @Autowired
    private StableMatchingSolver stableMatchingSolver;
    @Autowired
    private StableMatchingSolverRBO stableMatchingSolverRBO;
    @Autowired
    private OTMStableMatchingSolver stableMatchingOTMProblemDTO;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatching(@RequestBody StableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolver.solveStableMatching(object));
    }

    /*
    * Đây là phần chạy RBO (Request Body Optimization) để giải Stable Matching Problem
    * */
    @Async("taskExecutor")
    @PostMapping("/stable-matching-rbo-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatching(
            @RequestBody NewStableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolverRBO.solveStableMatching(object));
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-oto-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatchingOTO(@RequestBody StableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolver.solveStableMatchingOTO(object));
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-otm-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatchingOTM(@RequestBody StableMatchingOTMProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingOTMProblemDTO.solveStableMatching(object));
    }

    @Async("taskExecutor")
    @PostMapping("/game-theory-solver")
    public CompletableFuture<ResponseEntity<Response>> solveGameTheory(@RequestBody GameTheoryProblemDTO gameTheoryProblem) {
        return CompletableFuture.completedFuture(gameTheorySolver.solveGameTheory(gameTheoryProblem));
    }

    @Async("taskExecutor")
    @PostMapping("/problem-result-insights/{sessionCode}")
    public CompletableFuture<ResponseEntity<Response>> getProblemResultInsights(@RequestBody GameTheoryProblemDTO gameTheoryProblem,
                                                                                @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(gameTheorySolver.getProblemResultInsights(
                gameTheoryProblem,
                sessionCode));
    }

    @Async("taskExecutor")
    @PostMapping("/matching-problem-result-insights/{sessionCode}")
    public CompletableFuture<ResponseEntity<Response>> getMatchingResultInsights(@RequestBody StableMatchingProblemDTO object,
                                                                                 @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(stableMatchingSolver.getProblemResultInsights(
                object,
                sessionCode));
    }

    @Async("taskExecutor")
    @PostMapping("/rbo-matching-problem-result-insights/{sessionCode}")
    public CompletableFuture<ResponseEntity<Response>> getMatchingResultInsights(@RequestBody NewStableMatchingProblemDTO object,
                                                                                 @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(stableMatchingSolverRBO.getProblemResultInsights(
                object,
                sessionCode));
    }


    @Async("taskExecutor")
    @PostMapping("/otm-matching-problem-result-insights/{sessionCode}")
    public CompletableFuture<ResponseEntity<Response>> getOTMMatchingResultInsights(@RequestBody StableMatchingOTMProblemDTO object,
                                                                                 @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(stableMatchingOTMProblemDTO.getProblemResultInsights(
                object,
                sessionCode));
    }

}