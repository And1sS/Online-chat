package com.and1ss.onlinechat.utils.password_hasher;

import java.security.NoSuchAlgorithmException;

public interface PasswordHasher {
    String hashPassword(String password) throws NoSuchAlgorithmException;
}