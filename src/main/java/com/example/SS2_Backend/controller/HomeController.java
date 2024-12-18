package com.example.SS2_Backend.controller;

import com.example.SS2_Backend.dto.request.GameTheoryProblemDTO;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.dto.response.Response;
import com.example.SS2_Backend.service.GameTheorySolver;
import com.example.SS2_Backend.service.OTMStableMatchingSolver;
import com.example.SS2_Backend.service.StableProblemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomeController {

    @Autowired
    private GameTheorySolver gameTheorySolver;

    @Autowired
    private StableProblemService stableMatchingSolver;

    @Autowired
    private OTMStableMatchingSolver stableMatchingOTMProblemDTO;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatching(@RequestBody @Valid NewStableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolver.solve(object));
    }

//    @Async("taskExecutor")
//    @PostMapping("/stable-matching-oto-solver")
//    public CompletableFuture<ResponseEntity<Response>> solveStableMatchingOTO(@RequestBody StableMatchingProblemDTO object) {
//        return CompletableFuture.completedFuture(stableMatchingSolver.solveStableMatchingOTO(object));
//    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-otm-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatchingOTM(@RequestBody @Valid NewStableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingOTMProblemDTO.solve(object));
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
    public CompletableFuture<ResponseEntity<Response>> getMatchingResultInsights(@RequestBody @Valid NewStableMatchingProblemDTO object,
                                                                                 @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(stableMatchingSolver.getInsights(object,
                sessionCode));
    }

    @Async("taskExecutor")
    @PostMapping("/otm-matching-problem-result-insights/{sessionCode}")
    public CompletableFuture<ResponseEntity<Response>> getOTMMatchingResultInsights(@RequestBody @Valid NewStableMatchingProblemDTO object,
                                                                                    @PathVariable String sessionCode) {
        return CompletableFuture.completedFuture(stableMatchingOTMProblemDTO.getInsights(
                object,
                sessionCode));
    }

}