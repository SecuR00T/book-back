package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.common.SuccessResponse;
import com.bookvillage.backend.model.Coupon;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/api/coupons")
public class CouponController {
    private final InMemoryDataStore store;

    public CouponController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<Coupon> getCoupons(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getCoupons(keyword, status, page, pageSize);
    }

    @PostMapping
    public Coupon createCoupon(@RequestBody Coupon request) {
        return store.createCoupon(request);
    }

    @PutMapping("/{id}")
    public Coupon updateCoupon(@PathVariable String id, @RequestBody Map<String, Object> patch) {
        return store.updateCoupon(id, patch);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse deleteCoupon(@PathVariable String id) {
        store.deleteCoupon(id);
        return new SuccessResponse(true);
    }
}
