package com.example.SS2_Backend.controller;

import com.example.SS2_Backend.dto.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = Logger.getLogger("GlobalExceptionHandler");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        var errors = e.getFieldErrors().stream().map(fieldError -> "Field: " + fieldError.getField() + " Error: " + fieldError.getDefaultMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(Instant.now(), errors.toString(), request.getDescription(false));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RejectedExecutionException.class)
    public ResponseEntity<String> handleRejectedExecutionException(RejectedExecutionException ex) {
        logger.warning("Queue full!");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Server is busy. Please try again later.");
    }


}
