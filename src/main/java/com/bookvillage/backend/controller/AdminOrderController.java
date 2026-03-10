package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.Order;
import com.bookvillage.backend.request.OrderStatusUpdateRequest;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminOrderController")
@RequestMapping("/admin/api/orders")
public class AdminOrderController {
    private final InMemoryDataStore store;

    public AdminOrderController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<Order> getOrders(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String fulfillmentStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getOrders(keyword, paymentStatus, fulfillmentStatus, startDate, endDate, page, pageSize);
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable String id) {
        return store.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @RequestBody OrderStatusUpdateRequest request) {
        String paymentStatus = request == null ? null : request.paymentStatus;
        String fulfillmentStatus = request == null ? null : request.fulfillmentStatus;
        return store.updateOrderStatus(id, paymentStatus, fulfillmentStatus);
    }
}
