package com.bd.socialnetwork;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {
    @ExceptionHandler(ExistingLoginException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<ExistingLoginException> handleExistingLogin(ExistingLoginException exception) {
        return new ResponseEntity<>(exception,HttpStatus.PRECONDITION_FAILED);
    }
}
