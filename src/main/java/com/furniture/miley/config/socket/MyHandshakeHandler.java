package com.furniture.miley.config.socket;

import com.furniture.miley.delivery.model.StompPrincipal;
import com.furniture.miley.exception.customexception.ResourceNotFoundException;
import com.furniture.miley.security.jwt.JwtProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Component
public class MyHandshakeHandler extends DefaultHandshakeHandler {
    @Autowired
    private JwtProvider jwtProvider;
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearerToken = authHeaders.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);
                if (jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsernameFromToken(token);
                    return new StompPrincipal(username);
                }
            }
        }
        return null;
    }
}
