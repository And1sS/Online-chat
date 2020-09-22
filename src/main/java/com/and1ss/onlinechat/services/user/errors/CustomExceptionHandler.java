package com.and1ss.onlinechat.services.user.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Timestamp;
import java.time.Instant;

@ControllerAdvice
@RestController
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Data
    @AllArgsConstructor
    public class ApiError {
        private final Timestamp timestamp;
        private final String message;
    }

    @ExceptionHandler(InvalidLoginCredentialsException.class)
    public final ResponseEntity<Object> handleInvalidLoginCredentialsException(
            InvalidLoginCredentialsException ex
    ) {
        ApiError exceptionResponse = new ApiError(
                Timestamp.from(Instant.now()),
                ex.getMessage()
        );
        return new ResponseEntity(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidRegisterDataException.class)
    public final ResponseEntity<Object> handleInvalidRegisterDataException(
            InvalidRegisterDataException ex
    ) {
        ApiError exceptionResponse = new ApiError(
                Timestamp.from(Instant.now()),
                ex.getMessage()
        );
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}