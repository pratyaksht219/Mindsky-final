package com.example.mentalhealth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SessionResolver {

    private static final String SESSION_COOKIE = "SESSION_ID";

    public String resolveSessionId(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie c : cookies) {
                if (SESSION_COOKIE.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }

        String sessionId = UUID.randomUUID().toString();

        Cookie cookie = new Cookie(SESSION_COOKIE, sessionId);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 60);

        response.addCookie(cookie);

        return sessionId;
    }
}