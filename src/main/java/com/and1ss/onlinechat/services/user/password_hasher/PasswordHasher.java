package com.and1ss.onlinechat.services.user.password_hasher;

public interface PasswordHasher {
    String hashPassword(String password);
}