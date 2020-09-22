package com.and1ss.onlinechat.services.user.password_hasher;

import com.and1ss.onlinechat.services.user.password_hasher.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class PasswordHasherImpl implements PasswordHasher {

    @Override
    public String hashPassword(String password) {
        return password;
    }
}