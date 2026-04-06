package com.lms.www.fee.discount.service;

import com.lms.www.fee.dto.DiscountRequest;
import com.lms.www.fee.dto.DiscountResponse;
import java.util.List;

public interface DiscountService {
    DiscountResponse applyDiscount(DiscountRequest request);
    List<DiscountResponse> getDiscountsByUser(Long userId);
    List<DiscountResponse> getDiscountsByStructure(Long structureId);
    DiscountResponse approveDiscount(Long id);
    void deactivateDiscount(Long id);
}
