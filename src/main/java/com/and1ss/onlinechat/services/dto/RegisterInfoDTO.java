package com.and1ss.onlinechat.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class RegisterInfoDTO {
    @NonNull
    private final String name;
    @NonNull
    private final String surname;
    @NonNull
    private final String login;
    @NonNull
    private final String password;
}
