package com.lms.www.fee.penalty.service;

import com.lms.www.fee.dto.PenaltyResponse;
import com.lms.www.fee.dto.FeePenaltySlabRequest;
import com.lms.www.fee.dto.FeePenaltySlabResponse;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.penalty.entity.PenaltyType;
import com.lms.www.fee.penalty.entity.FeePenaltySlab;
import com.lms.www.fee.penalty.entity.FeePenalty;
import com.lms.www.fee.penalty.repository.FeePenaltyRepository;
import com.lms.www.fee.penalty.repository.FeePenaltySlabRepository;
import com.lms.www.security.UserContext;
import com.lms.www.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PenaltyServiceImpl implements PenaltyService {

    private final FeePenaltyRepository penaltyRepository;
    private final FeePenaltySlabRepository slabRepository;
    private final UserContext userContext;

    @Override
    public BigDecimal calculatePenalty(Long installmentId) {
        return slabRepository.findAll().stream()
                .filter(FeePenaltySlab::isActive)
                .findFirst()
                .map(s -> s.getPenaltyType() == PenaltyType.FIXED ? s.getValue() : s.getValue().multiply(BigDecimal.ONE))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    public PenaltyResponse applyPenalty(Long installmentId, BigDecimal amount, String reason) {
        FeePenalty penalty = FeePenalty.builder()
                .installmentId(installmentId)
                .amount(amount)
                .penaltyDate(LocalDate.now())
                .reason(reason)
                .build();
        FeePenalty savedPenalty = penaltyRepository.save(penalty);
        return FeeMapper.toResponse(java.util.Objects.requireNonNull(savedPenalty, "Saved penalty cannot be null"));
    }

    @Override
    public void waivePenalty(Long penaltyId) {
        Objects.requireNonNull(penaltyId, "Penalty ID cannot be null");
        FeePenalty penalty = penaltyRepository.findById(penaltyId)
                .orElseThrow(() -> new ResourceNotFoundException("Penalty not found"));
        penalty.setWaived(true);
        penalty.setWaivedBy(userContext.getCurrentUserId());
        penalty.setWaivedAt(LocalDateTime.now());
        penaltyRepository.save(penalty);
    }

    @Override
    public List<PenaltyResponse> getPenaltiesByInstallment(Long installmentId) {
        return penaltyRepository.findByInstallmentId(installmentId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeePenaltySlabResponse createPenaltySlab(FeePenaltySlabRequest request) {
        FeePenaltySlab slab = new FeePenaltySlab();
        slab.setFromDay(request.getFromDay());
        slab.setToDay(request.getToDay());
        slab.setPenaltyType(request.getPenaltyType());
        slab.setValue(request.getValue());
        slab.setActive(request.isActive());
        return FeeMapper.toResponse(slabRepository.save(slab));
    }

    @Override
    public List<FeePenaltySlabResponse> getAllPenaltySlabs() {
        return slabRepository.findAll().stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeePenaltySlabResponse> getSlabsByFeeStructure(Long feeStructureId) {
        return slabRepository.findByFeeStructureId(feeStructureId).stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeePenaltySlabResponse updatePenaltySlab(Long id, FeePenaltySlabRequest request) {
        Objects.requireNonNull(id, "Slab ID cannot be null");
        FeePenaltySlab slab = slabRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Slab not found"));
        slab.setFromDay(request.getFromDay());
        slab.setToDay(request.getToDay());
        slab.setPenaltyType(request.getPenaltyType());
        slab.setValue(request.getValue());
        slab.setActive(request.isActive());
        return FeeMapper.toResponse(slabRepository.save(slab));
    }

    @Override
    public void deletePenaltySlab(Long id) {
        Objects.requireNonNull(id, "Slab ID cannot be null");
        slabRepository.deleteById(id);
    }

    @Override
    public FeePenaltySlabResponse getSlabById(Long id) {
        Objects.requireNonNull(id, "Slab ID cannot be null");
        return slabRepository.findById(id)
                .map(FeeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Slab not found"));
    }
}
