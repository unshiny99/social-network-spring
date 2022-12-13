package com.bd.socialnetwork.Exception;

public class ExistingException extends RuntimeException {
    public ExistingException(String message) {
        super(message);
    }

    public ExistingException(String message, Throwable cause) {
        super(message, cause);
    }
}
