package com.bookvillage.backend.controller;

import com.bookvillage.backend.dto.CartRequest;
import com.bookvillage.backend.dto.GuestOrderLookupDto;
import com.bookvillage.backend.dto.OrderDto;
import com.bookvillage.backend.entity.Order;
import com.bookvillage.backend.repository.OrderRepository;
import com.bookvillage.backend.security.UserPrincipal;
import com.bookvillage.backend.service.LearningFeatureService;
import com.bookvillage.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final LearningFeatureService learningFeatureService;
    private final OrderRepository orderRepository;

    @PostMapping("/cart")
    public ResponseEntity<OrderDto> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CartRequest request) {
        OrderDto order = orderService.checkout(principal.getUserId(), request);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CartRequest request) {
        OrderDto order = orderService.checkout(principal.getUserId(), request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<OrderDto> orders = orderService.getOrdersByUserId(principal.getUserId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderDetail(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(principal.getUserId(), orderId));
    }

    @GetMapping("/lookup")
    public ResponseEntity<GuestOrderLookupDto> lookupOrder(@RequestParam String orderNumber) {
        return ResponseEntity.ok(orderService.getGuestLookupByOrderNumber(orderNumber));
    }

    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<Map<String, Object>> trackOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @RequestParam String trackingUrl) {
        return ResponseEntity.ok(learningFeatureService.trackOrder(principal.getUserId(), orderId, trackingUrl));
    }

    /**
     * Order status update endpoint.
     * Accepts any status value including COMPLETED without verifying previous state.
     * Step validation (PENDING -> PAID -> SHIPPED -> DELIVERED -> COMPLETED) is intentionally omitted.
     */
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        String newStatus = request != null ? request.get("status") : null;
        if (newStatus == null || newStatus.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "status is required"));
        }
        Order order = orderRepository.findById(orderId)
                .orElse(null);
        if (order == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Order not found"));
        }
        // No step validation: any status transition is accepted.
        String previousStatus = order.getStatus();
        order.setStatus(newStatus.trim().toUpperCase());
        orderRepository.save(order);

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "previousStatus", previousStatus != null ? previousStatus : "",
                "currentStatus", order.getStatus(),
                "message", "주문 상태가 변경되었습니다."
        ));
    }
}
