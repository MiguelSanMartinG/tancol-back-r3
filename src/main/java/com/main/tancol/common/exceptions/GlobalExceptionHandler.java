package com.main.tancol.common.exceptions;

import com.main.tancol.dashboard.exception.ConfiguracionKpiInvalidaException;
import com.main.tancol.common.exceptions.exeption.EmailAlreadyExist;
import com.main.tancol.common.exceptions.exeption.FilterNotFound;
import com.main.tancol.common.exceptions.exeption.GenericExeption;
import com.main.tancol.common.exeptions.IDNotFound;
import com.main.tancol.common.exceptions.exeption.PropiedaDuplicada;
import com.main.tancol.common.exceptions.records.RestErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestErrorResponse> handleInvalidJson(HttpMessageNotReadableException exception, HttpServletRequest request) {

        var response = new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(), exception.getMessage(),request.getRequestURI() , LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EmailAlreadyExist.class})
    public ResponseEntity<RestErrorResponse> handler(EmailAlreadyExist exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage(),request.getRequestURI() , LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);

    }

    @ExceptionHandler({GenericExeption.class})
    public ResponseEntity<RestErrorResponse> handler(GenericExeption exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage(), request.getRequestURI(), LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({IDNotFound.class})
    public ResponseEntity<RestErrorResponse> handler(IDNotFound exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.CONFLICT.value(), exception.getMessage(),request.getRequestURI() , LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PropiedaDuplicada.class})
    public ResponseEntity<RestErrorResponse> handler(PropiedaDuplicada exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.PRECONDITION_FAILED.value(), exception.getMessage(),request.getRequestURI() , LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    @ExceptionHandler({FilterNotFound.class})
    public ResponseEntity<RestErrorResponse> handler(FilterNotFound exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.NOT_FOUND.value(), exception.getMessage(),request.getRequestURI() , LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<RestErrorResponse> handleBadCredentials(BadCredentialsException exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.FORBIDDEN.value(), "La contraseña es incorrecta", request.getRequestURI(), LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<RestErrorResponse> handleAccessDenied(AccessDeniedException exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.FORBIDDEN.value(), exception.getMessage(), request.getRequestURI(), LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConfiguracionKpiInvalidaException.class)
    public ResponseEntity<RestErrorResponse> handleConfiguracionKpiInvalida(
            ConfiguracionKpiInvalidaException exception, HttpServletRequest request) {
        var response = new RestErrorResponse(
                HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request.getRequestURI(), LocalDateTime.now());
        log.error("{}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
