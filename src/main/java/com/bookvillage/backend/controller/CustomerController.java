package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.Customer;
import com.bookvillage.backend.model.Order;
import com.bookvillage.backend.request.CustomerMemberAccessUpdateRequest;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/api/customers")
public class CustomerController {
    private final InMemoryDataStore store;

    public CustomerController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<Customer> getCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getCustomers(keyword, grade, status, role, page, pageSize);
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable String id) {
        return store.getCustomer(id);
    }

    @GetMapping("/{id}/orders")
    public List<Order> getCustomerOrders(@PathVariable("id") String customerId) {
        return store.getCustomerOrders(customerId);
    }

    @PatchMapping("/{id}/member-access")
    public Customer updateCustomerMemberAccess(
            @PathVariable String id,
            @RequestBody CustomerMemberAccessUpdateRequest request
    ) {
        String status = request == null ? null : request.status;
        String memberRole = request == null ? null : request.memberRole;
        return store.updateCustomerMemberAccess(id, status, memberRole);
    }
}
