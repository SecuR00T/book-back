package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.common.SuccessResponse;
import com.bookvillage.backend.model.Product;
import com.bookvillage.backend.request.DeleteIdsRequest;
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
@RequestMapping("/admin/api/products")
public class ProductController {
    private final InMemoryDataStore store;

    public ProductController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping
    public PageResponse<Product> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getProducts(keyword, status, category, page, pageSize);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return store.getProduct(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product request) {
        return store.createProduct(request);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Map<String, Object> patch) {
        return store.updateProduct(id, patch);
    }

    @DeleteMapping
    public SuccessResponse deleteProducts(@RequestBody(required = false) DeleteIdsRequest request) {
        store.deleteProducts(request == null ? null : request.ids);
        return new SuccessResponse(true);
    }
}
