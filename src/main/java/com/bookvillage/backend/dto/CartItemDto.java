package com.bookvillage.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {
    private Long id;
    private Long bookId;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
}
