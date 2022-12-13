package com.bd.socialnetwork;

public class ExistingLoginException extends RuntimeException {
    public ExistingLoginException(String message) {
        super(message);
    }

    public ExistingLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
