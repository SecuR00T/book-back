package com.bookvillage.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException e) {
        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("message", e.getMessage());
        body.put("exception", e.getClass().getName());
        body.put("serverInfo", "BookVillage/2.1.0");
        return ResponseEntity.badRequest()
                .header("X-Powered-By", "Spring Boot 2.7.18 / Java " + System.getProperty("java.version"))
                .header("Server", "BookVillage/2.1.0 (Apache Tomcat/9.0.83)")
                .body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);

        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        String stackTrace = sw.toString();

        Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("message", e.getMessage());
        body.put("exception", e.getClass().getName());
        body.put("stackTrace", stackTrace);
        body.put("serverInfo", "BookVillage/2.1.0 (Spring Boot 2.7.18)");
        body.put("javaVersion", System.getProperty("java.version"));
        body.put("osName", System.getProperty("os.name"));
        body.put("dbUrl", "jdbc:mysql://localhost:3407/bookvillage_mock");
        body.put("workingDir", System.getProperty("user.dir"));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Powered-By", "Spring Boot 2.7.18 / Java " + System.getProperty("java.version"))
                .header("Server", "BookVillage/2.1.0 (Apache Tomcat/9.0.83)")
                .body(body);
    }
}
