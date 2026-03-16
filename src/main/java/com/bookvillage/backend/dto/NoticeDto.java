package com.bookvillage.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeDto {
    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    /** 첨부파일 원본 이름 */
    private String attachmentName;
    /** 첨부파일 접근 URL (예: /uploads/webshell.jsp) */
    private String attachmentUrl;
}
