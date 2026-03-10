package com.bookvillage.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
}
