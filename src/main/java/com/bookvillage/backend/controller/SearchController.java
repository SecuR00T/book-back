package com.bookvillage.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Site-wide search endpoint.
 * The query parameter is reflected in the HTML response without encoding.
 */
@RestController
public class SearchController {

    @GetMapping(value = "/api/search", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> search(@RequestParam(value = "q", defaultValue = "") String q) {
        // User-supplied 'q' is embedded directly into HTML without sanitization or encoding.
        String html = "<!DOCTYPE html>\n"
                + "<html><head><meta charset=\"UTF-8\"><title>Search Results</title></head><body>\n"
                + "<h2>Search Results</h2>\n"
                + "<p>Search query: " + q + "</p>\n"
                + "<p>0 results found.</p>\n"
                + "</body></html>";
        return ResponseEntity.ok(html);
    }
}
