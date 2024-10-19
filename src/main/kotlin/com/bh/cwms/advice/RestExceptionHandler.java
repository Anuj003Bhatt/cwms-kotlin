package com.bh.cwms.advice;


import com.bh.cwms.exception.AuthenticationFailedException;
import com.bh.cwms.exception.BadRequestException;
import com.bh.cwms.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(value = {NotFoundException.class})
    protected ResponseEntity<Object> handle404(RuntimeException ex, WebRequest request) {
        return responseFromError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {BadRequestException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handle500(RuntimeException ex) {
        return responseFromError(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AuthenticationFailedException.class})
    protected ResponseEntity<Object> handleAuthFailure(RuntimeException ex) {
        return responseFromError(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleGeneral(Exception ex) {
        String message = ex.getMessage();
        return new ResponseEntity<>(Map.of("error", message), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> responseFromError(RuntimeException ex, HttpStatus status) {
        String message = ex.getMessage();
        return new ResponseEntity<>(Map.of("error", message), status);
    }
}
