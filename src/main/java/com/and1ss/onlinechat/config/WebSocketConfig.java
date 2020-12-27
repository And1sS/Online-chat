package com.and1ss.onlinechat.config;

import com.and1ss.onlinechat.api.ws.base.HttpHandshakeInterceptor;
import com.and1ss.onlinechat.api.ws.base.WebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler webSocketHandler;

    private final HttpHandshakeInterceptor httpHandshakeInterceptor;

    @Autowired
    public WebSocketConfig(WebSocketHandler webSocketHandler,
                           HttpHandshakeInterceptor httpHandshakeInterceptor) {
        this.webSocketHandler = webSocketHandler;
        this.httpHandshakeInterceptor = httpHandshakeInterceptor;
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(webSocketHandler, "/api/ws")
                .addInterceptors(httpHandshakeInterceptor);
    }
}
