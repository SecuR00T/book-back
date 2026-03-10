package com.bookvillage.backend.controller;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Personalized greeting endpoint.
 * Evaluates the name parameter using the Spring Expression Language engine.
 */
@RestController
@RequestMapping("/api/greet")
public class GreetingController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> greet(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("input", name);

        try {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(name);
            Object result = exp.getValue();
            body.put("greeting", "Hello, " + (result != null ? result.toString() : "null") + "!");
            body.put("evaluated", result != null ? result.toString() : null);
        } catch (Exception e) {
            body.put("greeting", "Hello, " + name + "!");
            body.put("error", e.getMessage());
        }

        return ResponseEntity.ok(body);
    }
}
