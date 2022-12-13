package com.bd.socialnetwork.Exception;

public class NotFoundLoginException extends RuntimeException {
    public NotFoundLoginException(String message) {
        super(message);
    }

    public NotFoundLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
