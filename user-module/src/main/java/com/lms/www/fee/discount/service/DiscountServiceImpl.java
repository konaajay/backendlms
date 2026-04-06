package com.lms.www.fee.discount.service;

import com.lms.www.fee.discount.entity.FeeDiscount;
import com.lms.www.fee.discount.repository.FeeDiscountRepository;
import com.lms.www.fee.dto.DiscountRequest;
import com.lms.www.fee.dto.DiscountResponse;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class DiscountServiceImpl implements DiscountService {

    private final FeeDiscountRepository discountRepository;
    private final UserContext userContext;

    @Override
    public DiscountResponse applyDiscount(DiscountRequest request) {
        FeeDiscount discount = FeeDiscount.builder()
                .userId(request.getUserId())
                .feeStructureId(request.getFeeStructureId())
                .discountName(request.getDiscountName())
                .amount(request.getAmount())
                .reason(request.getReason())
                .discountScope(FeeDiscount.DiscountScope.valueOf(request.getScope()))
                .scopeId(request.getScopeId())
                .build();
        
        return mapToResponse(discountRepository.save(discount));
    }

    @Override
    public List<DiscountResponse> getDiscountsByUser(Long userId) {
        return discountRepository.findByUserId(userId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<DiscountResponse> getDiscountsByStructure(Long structureId) {
        return discountRepository.findByFeeStructureId(structureId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public DiscountResponse approveDiscount(Long id) {
        FeeDiscount discount = discountRepository.findById(id).orElseThrow();
        discount.setApprovedBy(userContext.getCurrentUserId());
        discount.setApprovedDate(LocalDate.now());
        return mapToResponse(discountRepository.save(discount));
    }

    @Override
    public void deactivateDiscount(Long id) {
        FeeDiscount discount = discountRepository.findById(id).orElseThrow();
        discount.setIsActive(false);
        discountRepository.save(discount);
    }

    private DiscountResponse mapToResponse(FeeDiscount discount) {
        return DiscountResponse.builder()
                .id(discount.getId())
                .userId(discount.getUserId())
                .amount(discount.getAmount())
                .scope(discount.getDiscountScope().name())
                .isActive(discount.getIsActive())
                .build();
    }
}
