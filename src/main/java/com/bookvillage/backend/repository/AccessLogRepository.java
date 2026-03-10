package com.bookvillage.backend.repository;

import com.bookvillage.backend.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    void deleteByUserId(Long userId);
}
