package com.bd.socialnetwork;

public class NotFoundLoginException extends RuntimeException {
    public NotFoundLoginException(String message) {
        super(message);
    }

    public NotFoundLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
