package com.example.SS2_Backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = Logger.getLogger("GlobalExceptionHandler");
    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<String> handleRejectedExecutionException(RejectedExecutionException ex) {
        logger.warning("Queue full!");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Server is busy. Please try again later.");
    }
}
