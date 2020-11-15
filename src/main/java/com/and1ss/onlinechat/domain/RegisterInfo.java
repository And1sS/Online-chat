package com.and1ss.onlinechat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class RegisterInfo {
    @NonNull
    private final String name;
    @NonNull
    private final String surname;
    @NonNull
    private final String login;
    @NonNull
    private final String password;
}
