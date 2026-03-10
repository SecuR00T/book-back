package com.bookvillage.backend.controller;

import com.bookvillage.backend.entity.Order;
import com.bookvillage.backend.repository.OrderRepository;
import com.bookvillage.backend.service.FileService;
import com.bookvillage.backend.service.SecurityLabService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SecurityLabService securityLabService;
    private final OrderRepository orderRepository;

    @Value("${file.lab-upload-path:./uploads/lab}")
    private String labUploadPath;

    @GetMapping("/api/download")
    public ResponseEntity<?> download(@RequestParam String file) {
        try {
            maybeRegenerateReceipt(file);
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

    private void maybeRegenerateReceipt(String file) {
        if (file == null) {
            return;
        }
        String trimmed = file.trim();
        if (!trimmed.startsWith("order_") || !trimmed.endsWith(".pdf")) {
            return;
        }

        String orderNumber = trimmed.substring("order_".length(), trimmed.length() - ".pdf".length());
        Order order = orderRepository.findByOrderNumber(orderNumber).orElse(null);
        if (order == null) {
            return;
        }

        // Always regenerate receipt with the latest template for existing orders.
        fileService.generateReceipt(order);
    }

    /**
     * General file upload endpoint.
     * Accepts any file type and stores it in the lab upload directory.
     * No file type validation is performed on the server side.
     */
    @PostMapping("/api/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "file is required"));
        }
        try {
            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.trim().isEmpty()) {
                originalName = "upload.bin";
            }
            // Remove path separators but keep the extension as-is
            originalName = originalName.replace("\\", "/");
            int slashIdx = originalName.lastIndexOf('/');
            if (slashIdx >= 0) {
                originalName = originalName.substring(slashIdx + 1);
            }

            File uploadDir = Paths.get(labUploadPath).toAbsolutePath().toFile();
            uploadDir.mkdirs();
            File dest = new File(uploadDir, originalName);
            file.transferTo(dest);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("fileName", originalName);
            body.put("filePath", dest.getAbsolutePath());
            body.put("fileUrl", "/uploads/" + originalName);
            body.put("size", file.getSize());
            body.put("contentType", file.getContentType());
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
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
