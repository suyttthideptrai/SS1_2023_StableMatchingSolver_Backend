package com.example.SS2_Backend.dto.response;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ExceptionResponse {
    Instant data;
    String message;
    String details;

    public ExceptionResponse(Instant data, String message, String details) {
        this.data = data;
        this.message = message;
        this.details = details;
    }
}
