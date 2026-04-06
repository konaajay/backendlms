package com.lms.www.marketing.service;

import com.lms.www.marketing.model.Coupon;
import com.lms.www.marketing.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminCouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findByDeletedFalse();
    }

    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.findByCodeAndDeletedFalse(coupon.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }
        coupon.setDeleted(false);
        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon updateCoupon(@org.springframework.lang.NonNull Long id, Coupon coupon) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        if (existing.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted coupon");
        }

        existing.setCode(coupon.getCode());
        existing.setDiscountType(coupon.getDiscountType());
        existing.setDiscountValue(coupon.getDiscountValue());
        existing.setDiscountCap(coupon.getDiscountCap());
        existing.setExpiryDate(coupon.getExpiryDate());
        existing.setMaxUsage(coupon.getMaxUsage());
        existing.setMinPurchaseAmount(coupon.getMinPurchaseAmount());
        existing.setPerUserLimit(coupon.getPerUserLimit());
        existing.setStatus(coupon.getStatus());
        
        return couponRepository.save(existing);
    }

    @Transactional
    public void deleteCoupon(@org.springframework.lang.NonNull Long id) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        existing.setDeleted(true);
        couponRepository.save(existing);
    }
}
