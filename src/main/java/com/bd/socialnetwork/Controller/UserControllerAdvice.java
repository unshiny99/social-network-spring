package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {
    @ExceptionHandler(ExistingException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<String> handleExistingLogin(ExistingException exception) {
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<String> handleNotFoundLogin(NotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.PRECONDITION_FAILED);
    }
}
