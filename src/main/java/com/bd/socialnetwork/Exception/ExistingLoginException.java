package com.bd.socialnetwork.Exception;

public class ExistingLoginException extends RuntimeException {
    public ExistingLoginException(String message) {
        super(message);
    }

    public ExistingLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
