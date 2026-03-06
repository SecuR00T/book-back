package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.InventoryLog;
import com.bookvillage.backend.model.InventoryProduct;
import com.bookvillage.backend.request.InventoryAdjustRequest;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/inventory")
public class InventoryController {
    private final InMemoryDataStore store;

    public InventoryController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping("/products")
    public PageResponse<InventoryProduct> getInventoryProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn13,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getInventoryProducts(keyword, author, isbn13, page, pageSize);
    }

    @GetMapping("/logs")
    public PageResponse<InventoryLog> getInventoryLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, name = "type") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getInventoryLogs(keyword, type, page, pageSize);
    }

    @PostMapping("/adjust")
    public InventoryLog adjustInventory(@RequestBody InventoryAdjustRequest request) {
        return store.adjustInventory(request);
    }
}
