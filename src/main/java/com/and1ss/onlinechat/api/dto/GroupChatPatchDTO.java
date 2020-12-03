package com.and1ss.onlinechat.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupChatPatchDTO {
    private String title;
    private String about;
}
