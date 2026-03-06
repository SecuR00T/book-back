package com.bookvillage.backend.controller;

import com.bookvillage.backend.common.PageResponse;
import com.bookvillage.backend.model.AccessLogEntry;
import com.bookvillage.backend.service.InMemoryDataStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/monitoring")
public class MonitoringController {
    private final InMemoryDataStore store;

    public MonitoringController(InMemoryDataStore store) {
        this.store = store;
    }

    @GetMapping("/access-logs")
    public PageResponse<AccessLogEntry> getAccessLogs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String endpoint,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String ipAddress,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return store.getAccessLogs(keyword, endpoint, method, ipAddress, page, pageSize);
    }
}
