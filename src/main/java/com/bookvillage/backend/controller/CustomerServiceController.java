package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.CustomerServiceInquiry;
import com.bookvillage.backend.request.CustomerServiceReplyRequest;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminCustomerServiceController")
@RequestMapping("/admin/api/customer-service")
public class CustomerServiceController {
    private final InMemoryDataStore store;

    public CustomerServiceController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<CustomerServiceInquiry> getInquiries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getCustomerServiceInquiries(keyword, status, page, pageSize);
    }

    @PatchMapping("/{id}/reply")
    public CustomerServiceInquiry replyInquiry(
            @PathVariable String id,
            @RequestBody(required = false) CustomerServiceReplyRequest request
    ) {
        String answer = request == null ? null : request.answer;
        return store.replyCustomerServiceInquiry(id, answer);
    }
}
