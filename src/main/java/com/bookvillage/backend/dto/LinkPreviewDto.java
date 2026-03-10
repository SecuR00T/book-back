package com.bookvillage.backend.dto;

import lombok.Data;

@Data
public class LinkPreviewDto {
    private String url;
    private String title;
    private String thumbnailUrl;
    private String status;
}
