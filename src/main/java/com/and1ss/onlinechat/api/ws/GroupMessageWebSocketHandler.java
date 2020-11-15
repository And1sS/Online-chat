package com.and1ss.onlinechat.api.ws;

import com.and1ss.onlinechat.api.ws.base.AbstractWebSocketHandler;
import com.and1ss.onlinechat.services.GroupChatMessageService;
import com.and1ss.onlinechat.services.GroupChatService;
import com.and1ss.onlinechat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Controller
public class GroupMessageWebSocketHandler extends AbstractWebSocketHandler {

    private final GroupChatService groupChatService;

    private final GroupChatMessageService groupChatMessageService;

    private final UserService userService;

    @Autowired
    public GroupMessageWebSocketHandler(
            GroupChatService groupChatService,
            GroupChatMessageService groupChatMessageService,
            UserService userService
    ) {
        this.groupChatService = groupChatService;
        this.groupChatMessageService = groupChatMessageService;
        this.userService = userService;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
        System.out.println(message);

        sendToUsersWhoseIdIn(
                List.of(
                        "efa5a6c4-6104-406a-9156-241a58ecb1be",
                        "d15e0db5-05c3-436b-8c58-1fa2e74b3a29",
                        "f5c5c1ee-abd3-4fe4-b14a-cf76690fc529",
                        "666e9eeb-a8a6-446d-aeb3-92261222fa22"
                ), message);
        //session.sendMessage(new BinaryMessage(ByteBuffer.wrap("hello blyat!".getBytes(Charset.defaultCharset()))));
//        throw new InternalServerException();
    }
}
