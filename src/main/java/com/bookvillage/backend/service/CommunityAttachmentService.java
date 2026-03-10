package com.bookvillage.backend.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityAttachmentService {

    @Value("${file.lab-upload-path:./uploads/lab}")
    private String uploadPath;

    private Path basePath;
    private final JdbcTemplate jdbcTemplate;

    public CommunityAttachmentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        basePath = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + basePath, e);
        }
    }

    public UploadedFile store(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String ext = extensionOf(originalName);
        String storedName = UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
        Path target = basePath.resolve(storedName);

        try {
            Files.write(target, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        String absolutePath = target.toAbsolutePath().toString();
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        jdbcTemplate.update(
                "INSERT INTO lab_uploaded_files (user_id, original_name, stored_name, absolute_path, content_type, file_size) VALUES (?,?,?,?,?,?)",
                userId, originalName, storedName, absolutePath, contentType, file.getSize()
        );

        return new UploadedFile(originalName, storedName, absolutePath, contentType, file.getSize());
    }

    public List<UploadedFileRow> listFiles() {
        try {
            return jdbcTemplate.query(
                    "SELECT id, user_id, original_name, stored_name, absolute_path, content_type, file_size, uploaded_at " +
                            "FROM lab_uploaded_files ORDER BY uploaded_at DESC LIMIT 100",
                    (rs, i) -> new UploadedFileRow(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("original_name"),
                            rs.getString("stored_name"),
                            rs.getString("absolute_path"),
                            rs.getString("content_type"),
                            rs.getLong("file_size"),
                            rs.getTimestamp("uploaded_at").toLocalDateTime()
                    )
            );
        } catch (Exception e) {
            return List.of();
        }
    }

    public ServedFile serve(String storedName) {
        String safeName = Paths.get(storedName).getFileName().toString();
        Path filePath = basePath.resolve(safeName);

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + safeName);
        }

        String ext = extensionOf(safeName).toLowerCase();
        String contentType = resolveContentType(ext);

        try {
            InputStream is = Files.newInputStream(filePath);
            return new ServedFile(is, contentType, safeName);
        } catch (IOException e) {
            throw new RuntimeException("파일 읽기 실패", e);
        }
    }

    private String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1);
    }

    private String resolveContentType(String ext) {
        switch (ext) {
            case "jsp": case "jspx": return "text/plain; charset=UTF-8";
            case "asp": case "aspx": return "text/plain; charset=UTF-8";
            case "php":              return "text/plain; charset=UTF-8";
            case "html": case "htm": return "text/html; charset=UTF-8";
            case "txt":              return "text/plain; charset=UTF-8";
            case "pdf":              return "application/pdf";
            case "png":              return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            default:                 return "application/octet-stream";
        }
    }

    @Getter
    public static class UploadedFile {
        private final String originalName;
        private final String storedName;
        private final String absolutePath;
        private final String contentType;
        private final long fileSize;

        public UploadedFile(String originalName, String storedName, String absolutePath,
                            String contentType, long fileSize) {
            this.originalName = originalName;
            this.storedName = storedName;
            this.absolutePath = absolutePath;
            this.contentType = contentType;
            this.fileSize = fileSize;
        }
    }

    @Getter
    public static class UploadedFileRow {
        private final long id;
        private final long userId;
        private final String originalName;
        private final String storedName;
        private final String absolutePath;
        private final String contentType;
        private final long fileSize;
        private final LocalDateTime uploadedAt;

        public UploadedFileRow(long id, long userId, String originalName, String storedName,
                               String absolutePath, String contentType, long fileSize, LocalDateTime uploadedAt) {
            this.id = id;
            this.userId = userId;
            this.originalName = originalName;
            this.storedName = storedName;
            this.absolutePath = absolutePath;
            this.contentType = contentType;
            this.fileSize = fileSize;
            this.uploadedAt = uploadedAt;
        }
    }

    @Getter
    public static class ServedFile {
        private final InputStream inputStream;
        private final String contentType;
        private final String filename;

        public ServedFile(InputStream inputStream, String contentType, String filename) {
            this.inputStream = inputStream;
            this.contentType = contentType;
            this.filename = filename;
        }
    }
}
