package com.and1ss.onlinechat.api.ws.base;

import com.and1ss.onlinechat.api.ws.dto.ChatWebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;

import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.text.SimpleDateFormat;

@Service
public class WebSocketMessageMapper {
    private static Charset charset = StandardCharsets.UTF_8;
    private static CharsetDecoder decoder = charset.newDecoder();
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    public static ByteBuffer stringToByteBuffer(String message) {
        return charset.encode(message);
    }

    public static String byteBufferToString(ByteBuffer buffer) throws CharacterCodingException {
        int oldPosition = buffer.position();
        String data = decoder.decode(buffer).toString();
        buffer.position(oldPosition);
        return data;
    }

    public static BinaryMessage webSocketMessageToBinaryMessage(ChatWebSocketMessage<?> message)
            throws JsonProcessingException {
        return new BinaryMessage(stringToByteBuffer(objectMapper.writeValueAsString(message)));
    }
}
