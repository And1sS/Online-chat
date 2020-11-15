package com.and1ss.onlinechat.api.ws.base;

import com.and1ss.onlinechat.api.ws.dto.WebSocketMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

@Service
public class WebSocketMessageMapper {
    private static Charset charset = StandardCharsets.UTF_8;
    private static CharsetEncoder encoder = charset.newEncoder();
    private static CharsetDecoder decoder = charset.newDecoder();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static ByteBuffer stringToByteBuffer(String message) throws CharacterCodingException {
        return encoder.encode(CharBuffer.wrap(message));
    }

    public static String byteBufferToString(ByteBuffer buffer) throws CharacterCodingException {
        int oldPosition = buffer.position();
        String data = decoder.decode(buffer).toString();
        buffer.position(oldPosition);
        return data;
    }

    public static <T> BinaryMessage webSocketMessageToBinaryMessage(WebSocketMessage<Object> message)
            throws JsonProcessingException, CharacterCodingException {
        final var messageAsString = objectMapper.writeValueAsString(message);
        return new BinaryMessage(stringToByteBuffer(messageAsString));
    }
}
