package com.and1ss.onlinechat.api.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessageCreationDTO {
    @NonNull
    private String contents;
}
