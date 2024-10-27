package com.example.SS2_Backend.dto.response;

import com.example.SS2_Backend.util.ErrorMapper;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class InvalidationException {
    private final ErrorMapper errorMapper;

    public InvalidationException(
            ErrorMapper errorMapper
    ) {
        this.errorMapper = errorMapper;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Response> handleException(Throwable e) {
        if (e instanceof ClientAbortException) {
            return null; //socket is closed, cannot return any response
        } else {
            return new ResponseEntity<Response>(this.errorMapper.createErrorMap(e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleException(RuntimeException e) {
        return new ResponseEntity<Response>(this.errorMapper.createErrorMap(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder strBuilder = new StringBuilder();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName;
            try {
                fieldName = ((FieldError) error).getField();

            } catch (ClassCastException ex) {
                fieldName = error.getObjectName();
            }
            String message = error.getDefaultMessage();
            strBuilder.append(String.format("%s: %s\n", fieldName, message));
        });

        return new ResponseEntity<>(errorMapper.createErrorMap(strBuilder.substring(0, strBuilder.length()-1)), HttpStatus.BAD_REQUEST);
    }

}
