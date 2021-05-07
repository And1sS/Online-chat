package com.and1ss.onlinechat.api.ws.mappers;

import com.and1ss.onlinechat.api.ws.dto.WsGroupMessagePatchDTO;
import com.and1ss.onlinechat.services.dto.GroupMessagePatchDTO;

public class WsGroupMessageMapper {
    public static GroupMessagePatchDTO toGroupMessagePatchDTO(WsGroupMessagePatchDTO patchDTO) {
        return new GroupMessagePatchDTO(patchDTO.getContents(), null);
    }
}
