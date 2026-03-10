package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.Review;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminReviewController")
@RequestMapping("/admin/api/reviews")
public class AdminReviewController {
    private final InMemoryDataStore store;

    public AdminReviewController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<Review> getReviews(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getReviews(keyword, status, page, pageSize);
    }

    @PatchMapping("/{id}/toggle")
    public Review toggleStatus(@PathVariable String id) {
        return store.toggleReviewStatus(id);
    }
}
