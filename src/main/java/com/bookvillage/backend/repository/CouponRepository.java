package com.bookvillage.backend.repository;

import com.bookvillage.backend.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
