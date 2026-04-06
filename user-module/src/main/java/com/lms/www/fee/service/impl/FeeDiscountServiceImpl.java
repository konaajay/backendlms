package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.FeeDiscountRequest;
import com.lms.www.fee.dto.FeeDiscountResponse;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.discount.entity.FeeDiscount;
import com.lms.www.fee.discount.repository.FeeDiscountRepository;
import com.lms.www.fee.service.FeeDiscountService;
import com.lms.www.security.UserContext;
import com.lms.www.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FeeDiscountServiceImpl implements FeeDiscountService {

    private final FeeDiscountRepository feeDiscountRepository;
    private final UserContext userContext;

    @Override
    public FeeDiscountResponse create(FeeDiscountRequest request) {
        // Mapping logic (using FeeMapper or builder)
        FeeDiscount discount = FeeDiscount.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .build();
        return FeeMapper.toResponse(feeDiscountRepository.save(discount));
    }

    @Override
    public FeeDiscountResponse getById(Long id) {
        return FeeMapper.toResponse(feeDiscountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found")));
    }

    @Override
    public List<FeeDiscountResponse> getAll() {
        return feeDiscountRepository.findAll().stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeeDiscountResponse> getByUserId(Long userId) {
        return feeDiscountRepository.findByUserId(userId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeeDiscountResponse> getByUserIdSecure(Long userId) {
        if (!userContext.isAdmin() && !userContext.getCurrentUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        return getByUserId(userId);
    }

    @Override
    public FeeDiscountResponse update(Long id, FeeDiscountRequest request) {
        FeeDiscount existing = feeDiscountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        existing.setAmount(request.getAmount());
        existing.setReason(request.getReason());
        return FeeMapper.toResponse(feeDiscountRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        feeDiscountRepository.deleteById(id);
    }
}
