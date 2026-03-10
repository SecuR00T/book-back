package com.bookvillage.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostics")
public class DiagnosticsController {

    /**
     * Network diagnostics endpoint.
     * Accepts a host parameter and runs a connectivity check.
     * The host value is passed directly to the OS command without sanitization.
     */
    @PostMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping(@RequestBody Map<String, String> request) {
        String host = request != null ? request.get("host") : null;
        if (host == null || host.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "host is required"));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("host", host);

        try {
            String os = System.getProperty("os.name", "").toLowerCase();
            ProcessBuilder pb;
            if (os.contains("win")) {
                pb = new ProcessBuilder("cmd.exe", "/c", "ping -n 1 " + host);
            } else {
                pb = new ProcessBuilder("/bin/sh", "-c", "ping -c 1 " + host);
            }
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 30) {
                    output.append(line).append('\n');
                    lineCount++;
                }
            }
            process.waitFor();
            body.put("result", output.toString());
        } catch (Exception e) {
            body.put("result", "");
            body.put("error", e.getMessage());
        }

        return ResponseEntity.ok(body);
    }

    /**
     * TRACE method echo endpoint.
     * Returns the received request headers back to the client.
     */
    @RequestMapping(value = "/trace", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.PATCH, RequestMethod.OPTIONS, RequestMethod.TRACE})
    public ResponseEntity<Map<String, Object>> traceRequest(HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("method", request.getMethod());
        body.put("requestURI", request.getRequestURI());
        body.put("queryString", request.getQueryString());

        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        body.put("headers", headers);
        body.put("remoteAddr", request.getRemoteAddr());
        body.put("serverInfo", "BookVillage/2.1.0 (Apache Tomcat/9.0.83, Spring Boot 2.7.18)");
        return ResponseEntity.ok(body);
    }

    /**
     * Server information endpoint.
     * Exposes detailed system and runtime information.
     */
    @GetMapping("/server-info")
    public ResponseEntity<Map<String, Object>> serverInfo() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("application", "BookVillage Mock");
        body.put("version", "2.1.0");
        body.put("framework", "Spring Boot 2.7.18");
        body.put("server", "Apache Tomcat/9.0.83");
        body.put("javaVersion", System.getProperty("java.version"));
        body.put("javaVendor", System.getProperty("java.vendor"));
        body.put("osName", System.getProperty("os.name"));
        body.put("osVersion", System.getProperty("os.version"));
        body.put("osArch", System.getProperty("os.arch"));
        body.put("userDir", System.getProperty("user.dir"));
        body.put("dbUrl", "jdbc:mysql://localhost:3407/bookvillage_mock");
        body.put("dbDriver", "com.mysql.cj.jdbc.Driver");
        body.put("fileStoragePath", "./uploads/receipts");
        body.put("labUploadPath", "./uploads/lab");
        body.put("maxMemory", Runtime.getRuntime().maxMemory());
        body.put("totalMemory", Runtime.getRuntime().totalMemory());
        body.put("freeMemory", Runtime.getRuntime().freeMemory());
        body.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        return ResponseEntity.ok(body);
    }
}
