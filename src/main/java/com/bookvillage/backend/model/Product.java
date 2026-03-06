package com.bookvillage.backend.model;

import java.util.ArrayList;
import java.util.List;

public class Product {
    public String id;
    public String title;
    public String subtitle;
    public String author;
    public String publisher;
    public String publishedDate;
    public String isbn13;
    public String category;
    public List<String> tags = new ArrayList<>();
    public int price;
    public Integer salePrice;
    public int stock;
    public String status;
    public String description;
    public List<String> images = new ArrayList<>();
    public String createdAt;
    public String updatedAt;
}
