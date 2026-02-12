package com.chessopeningstats.backend.exception.handler;

import com.chessopeningstats.backend.exception.handler.error.ErrorResponse;
import com.chessopeningstats.backend.exception.handler.error.ExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class WebExceptionHandler {

    private final ExceptionConverter exceptionConverter;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = exceptionConverter.convert(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }
}
