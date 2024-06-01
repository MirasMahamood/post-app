package com.miras.post.exception;

public class NotAllowedToEditException extends RuntimeException {
    public NotAllowedToEditException(String message) {
        super(message);
    }
}