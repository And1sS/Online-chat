package com.and1ss.onlinechat.services.user.errors;

public class InvalidLoginCredentialsException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Incorrect login or password";
    }
}
