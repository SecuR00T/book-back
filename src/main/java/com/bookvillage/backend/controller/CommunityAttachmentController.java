package com.bookvillage.backend.controller;

import com.bookvillage.backend.security.UserPrincipal;
import com.bookvillage.backend.service.CommunityAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityAttachmentController {

    private final CommunityAttachmentService communityAttachmentService;

    @PostMapping("/attachments")
    public ResponseEntity<?> upload(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        Long userId = principal != null ? principal.getUserId() : null;
        CommunityAttachmentService.UploadedFile stored = communityAttachmentService.store(userId, file);

        String accessUrl = "/uploads/" + stored.getStoredName();
        return ResponseEntity.ok(Map.of(
                "message", "파일 업로드 성공",
                "originalName", stored.getOriginalName(),
                "storedName", stored.getStoredName(),
                "absolutePath", stored.getAbsolutePath(),
                "contentType", stored.getContentType(),
                "fileSize", stored.getFileSize(),
                "accessUrl", accessUrl
        ));
    }

    @GetMapping("/attachments")
    public ResponseEntity<List<CommunityAttachmentService.UploadedFileRow>> listFiles() {
        return ResponseEntity.ok(communityAttachmentService.listFiles());
    }

    @GetMapping("/attachments/{filename:.+}")
    public ResponseEntity<InputStreamResource> serveFile(@PathVariable String filename) {
        CommunityAttachmentService.ServedFile served = communityAttachmentService.serve(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + served.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(served.getContentType()))
                .body(new InputStreamResource(served.getInputStream()));
    }
}
