package com.lms.www.fee.service;

import com.lms.www.fee.dto.FeeDiscountRequest;
import com.lms.www.fee.dto.FeeDiscountResponse;
import java.util.List;

public interface FeeDiscountService {
    FeeDiscountResponse create(FeeDiscountRequest request);
    FeeDiscountResponse getById(Long id);
    List<FeeDiscountResponse> getAll();
    List<FeeDiscountResponse> getByUserId(Long userId);
    List<FeeDiscountResponse> getByUserIdSecure(Long userId);
    FeeDiscountResponse update(Long id, FeeDiscountRequest request);
    void delete(Long id);
}
