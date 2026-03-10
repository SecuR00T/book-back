package com.bookvillage.mock.controller;

import com.bookvillage.mock.service.FileService;
import com.bookvillage.mock.service.SecurityLabService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SecurityLabService securityLabService;

    @GetMapping("/api/download")
    public ResponseEntity<?> download(@RequestParam String file) {
        try {
            Resource resource = fileService.loadFileAsResource(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/api/files")
    public ResponseEntity<?> listFiles(@RequestParam(defaultValue = ".") String dir) {
        try {
            Path path = Paths.get(dir).toAbsolutePath().normalize();
            return ResponseEntity.ok(Map.of(
                    "directory", path.toString(),
                    "files", Files.list(path)
                            .map(p -> p.getFileName().toString())
                            .collect(Collectors.toList())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
