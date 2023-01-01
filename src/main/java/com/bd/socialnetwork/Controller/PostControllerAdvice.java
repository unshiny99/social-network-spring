package com.bd.socialnetwork.Controller;

import com.bd.socialnetwork.Exception.ExistingException;
import com.bd.socialnetwork.Exception.InvalidParameterException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PostControllerAdvice {
    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException exception) {
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.PRECONDITION_FAILED);
    }
}
