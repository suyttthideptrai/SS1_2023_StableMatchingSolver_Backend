package com.example.SS2_Backend.controller;

import com.example.SS2_Backend.dto.request.GameTheoryProblemDTO;
import com.example.SS2_Backend.dto.request.NewStableMatchingProblemDTO;
import com.example.SS2_Backend.dto.request.StableMatchingProblemDTO;
import com.example.SS2_Backend.dto.response.Response;
import com.example.SS2_Backend.util.ErrorMapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.SS2_Backend.service.GameTheorySolver;
import com.example.SS2_Backend.service.StableMatchingSolver;
import com.example.SS2_Backend.service.StableMatchingSolverRBO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
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

    private ErrorMapper errorMapper;

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
            @Valid @RequestBody NewStableMatchingProblemDTO object,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors() || object.isEvaluateFunctionValid()) {
            List<String> errors = bindingResult.
                    getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(new ResponseEntity<>(new Response(
                    400,
                    errors.toString(),
                    // "Invalidated Data. Please check your input data!",
                    null
            ), HttpStatus.BAD_REQUEST));
        }
        if (object.isEvaluateFunctionValid()) {
            return CompletableFuture.completedFuture(new ResponseEntity<>(new Response(
                    400,
                    "Invalid evaluateFunctions, please retry.",
                    // "Invalidated Data. Please check your input data!",
                    null
            ), HttpStatus.BAD_REQUEST));
        }
        return CompletableFuture.completedFuture(stableMatchingSolverRBO.solveStableMatching(object));
    }

    @Async("taskExecutor")
    @PostMapping("/stable-matching-oto-solver")
    public CompletableFuture<ResponseEntity<Response>> solveStableMatchingOTO(@RequestBody StableMatchingProblemDTO object) {
        return CompletableFuture.completedFuture(stableMatchingSolver.solveStableMatchingOTO(object));
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