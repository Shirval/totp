package com.shirval.controller;

import com.shirval.exception.TotpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<?> handle(TotpException ex) {
        System.out.println(ex);
        return ResponseEntity
                .status(ex.geHttpCode())
                .body(ex.getPublicError());
    }

    @ExceptionHandler
    public ResponseEntity<?> handle(Exception ex) {
        System.out.println(ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TotpException.publicError);
    }
}
