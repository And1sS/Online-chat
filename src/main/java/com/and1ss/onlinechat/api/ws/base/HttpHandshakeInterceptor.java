package com.and1ss.onlinechat.api.ws.base;

import com.and1ss.onlinechat.domain.AccountInfo;
import com.and1ss.onlinechat.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final UserService userService;

    @Autowired
    public HttpHandshakeInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            var authHeader = request.getHeaders()
                    .get("Authorization");

            if (authHeader == null) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }

            String bearerAuthEncoded = String.join(" ", authHeader);

            try {
                String encodedAccessToken = bearerAuthEncoded
                        .replaceAll("\\[]", "")
                        .replaceFirst("Basic\\s", "");

                byte[] decodedBytes = Base64.getDecoder().decode(encodedAccessToken);
                String decodedAccessToken = new String(decodedBytes).replaceAll(":", "");

                AccountInfo user = userService.authorizeUserByAccessToken(decodedAccessToken);

                HttpSession session = servletRequest.getServletRequest().getSession();
                attributes.put("sessionId", session.getId());
                attributes.put("userId", user.getId().toString());
            } catch (Exception e) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return false;
            }
        }
        return true;
    }

    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {}
}
