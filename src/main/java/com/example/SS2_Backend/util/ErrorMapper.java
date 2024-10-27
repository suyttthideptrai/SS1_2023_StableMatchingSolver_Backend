package com.example.SS2_Backend.util;


import com.example.SS2_Backend.dto.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorMapper {

    /**
     * Creates map with key: "message" and value: exception's message.
     *
     * @param e - the thrown exception
     * @return the created map
     */
    public Response createErrorMap(Throwable e) {
        return new Response(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                null
        );
    }

    public Response createErrorMap(String message) {
        return new Response(
                HttpStatus.BAD_REQUEST.value(),
                message,
                null
        );
    }
}