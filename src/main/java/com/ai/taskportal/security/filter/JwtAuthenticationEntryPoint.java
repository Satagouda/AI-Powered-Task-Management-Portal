package com.ai.taskportal.security.filter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {

        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{" +
                "\"status\":401," +
                "\"message\":\"Unauthorized - Invalid or missing token\"," +
                "\"timestamp\":\"" + System.currentTimeMillis() + "\"" +
                "}");
    }
}
