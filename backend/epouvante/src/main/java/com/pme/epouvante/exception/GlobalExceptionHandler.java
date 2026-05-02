package com.pme.epouvante.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> erreur = new HashMap<>();

        int nombreErreurs = ex.getBindingResult().getFieldErrors().size();

        if (nombreErreurs > 1) {
            erreur.put("message", "Les champs obligatoires sont manquants");
        } else {
            erreur.put("message",
                    ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
        }

        return erreur;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        return Map.of("message", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbidden(ForbiddenException ex) {
        return Map.of("message", ex.getMessage());
    }
}