package com.chessopeningstats.backend.exception.handler;

import com.chessopeningstats.backend.exception.*;
import com.chessopeningstats.backend.web.dto.ApiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponseDto<?>> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.error("Authentication failed"));
    }

    // 409 Conflict
    @ExceptionHandler({
            UsernameAlreadyExistsException.class,
            NicknameAlreadyExistsException.class,
            PlayerAlreadyLinkedException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponseDto<?>> handleConflictException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponseDto.error(e.getMessage()));
    }

    // 404 Not Found
    @ExceptionHandler({
            PlayerNotFoundException.class,
            AccountNotFoundException.class,
            UsernameNotFoundOnPlatformException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponseDto<?>> handleNotFoundException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDto.error(e.getMessage()));
    }

    // 429 Too Many Requests
    @ExceptionHandler({
            RateLimitExceededException.class,
            TooManySyncRequestException.class
    })
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ResponseEntity<ApiResponseDto<?>> handleTooManyRequestsException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponseDto.error(e.getMessage()));
    }

    // 400 Bad Request
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponseDto<?>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error(e.getMessage()));
    }
}
