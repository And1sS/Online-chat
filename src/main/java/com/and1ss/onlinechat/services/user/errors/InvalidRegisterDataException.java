package com.and1ss.onlinechat.services.user.errors;

public class InvalidRegisterDataException extends RuntimeException {
    public InvalidRegisterDataException(String message) {
        super(message);
    }
}
