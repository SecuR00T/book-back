package com.bookvillage.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/admin/api/media")
public class MediaController {

    private final Path basePath = Paths.get("uploads", "admin-products").toAbsolutePath().normalize();

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path target = basePath.resolve(filename).normalize();
            if (!target.startsWith(basePath)) {
                return ResponseEntity.badRequest().build();
            }

            Resource resource = new UrlResource(target.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
            String lower = filename.toLowerCase();
            if (lower.endsWith(".png")) contentType = MediaType.IMAGE_PNG;
            else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG;
            else if (lower.endsWith(".gif")) contentType = MediaType.IMAGE_GIF;
            else if (lower.endsWith(".webp")) contentType = MediaType.parseMediaType("image/webp");

            return ResponseEntity.ok().contentType(contentType).body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
