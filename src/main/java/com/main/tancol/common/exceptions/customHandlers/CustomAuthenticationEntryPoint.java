package com.main.tancol.common.exceptions.customHandlers;

import com.main.tancol.common.exceptions.records.RestErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        logger.error(authException.getMessage() + request.getRemoteUser());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Object originalUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String requestUri = originalUri != null ? originalUri.toString() : request.getRequestURI();
        response.getWriter().write(new RestErrorResponse(401, "Por favor inicie session", requestUri, LocalDateTime.now()).toString());
    }
}