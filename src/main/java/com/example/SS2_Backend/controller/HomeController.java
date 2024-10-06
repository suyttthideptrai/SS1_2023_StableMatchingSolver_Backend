package com.example.SS2_Backend.controller;

import com.example.SS2_Backend.dto.request.GameTheoryProblemDTO;
import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.dto.response.Response;
import com.example.SS2_Backend.service.GameTheorySolver;
import com.example.SS2_Backend.service.StableMatchingSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @Autowired
    private GameTheorySolver gameTheorySolver;
    @Autowired
    private StableMatchingSolver stableMatchingSolver;


    @GetMapping("/")
    public String home() {
        return "index";
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatching(@RequestBody StableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolver.solveStableMatching(object));
    }


    @Async("taskExecutor")
    @GetMapping("/test")
    public CompletableFuture<ResponseEntity<Set<String>>> test() throws InterruptedException {
        logger.info("Test Called");
        //Thread.sleep(5000);
        return CompletableFuture.completedFuture(ResponseEntity.ok(Set.of("Tst",
                "Test",
                "Test1",
                "Test2",
                "Test3",
                "Test4")));
    }

    @Async("taskExecutor")
    @PostMapping("/game-theory-solver")
    public CompletableFuture<ResponseEntity<Response>> solveGameTheory(@RequestBody GameTheoryProblemDTO gameTheoryProblem) {
        return CompletableFuture.completedFuture(gameTheorySolver.solveGameTheory(gameTheoryProblem));
    }

    //	@PostMapping("/problem-result-insights/{sessionCode}")
//	public ResponseEntity<Response> getProblemResultInsights(@RequestBody GameTheoryProblemDTO gameTheoryProblem, @PathVariable String sessionCode) {
//		return gameTheorySolver.getProblemResultInsights(gameTheoryProblem, sessionCode);
//	}
//	@PostMapping("/matching-problem-result-insights/{sessionCode}")
//	public ResponseEntity<Response> getMatchingResultInsights(@RequestBody StableMatchingProblemDTO object, @PathVariable String sessionCode) {
//		return stableMatchingSolver.getProblemResultInsights(object, sessionCode);
//	}
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

}