package com.bookvillage.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPostRequest {

    private String title;
    private String content;
}
