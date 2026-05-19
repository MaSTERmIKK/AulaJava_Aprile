package com.example.demo.Run;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.Run.exeption.RunNotFoundException;
import com.example.demo.Run.record.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler 
{
    @ExceptionHandler(RunNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRunNotFound(RunNotFoundException ex)
    {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
