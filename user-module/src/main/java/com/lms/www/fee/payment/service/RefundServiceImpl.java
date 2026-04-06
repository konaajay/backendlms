package com.lms.www.fee.payment.service;

import com.lms.www.fee.dto.RefundRequest;
import com.lms.www.fee.dto.RefundResponse;
import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.payment.entity.StudentFeeRefund;
import com.lms.www.fee.payment.repository.StudentFeeRefundRepository;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RefundServiceImpl implements RefundService {

    private final StudentFeeRefundRepository refundRepository;
    private final UserContext userContext;

    @Override
    public RefundResponse createRefund(RefundRequest request) {
        StudentFeeRefund refund = StudentFeeRefund.builder()
                .allocationId(request.getAllocationId())
                .userId(userContext.getCurrentUserId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .status("PENDING")
                .build();
        
        refund = refundRepository.save(refund);
        return mapToResponse(refund);
    }

    @Override
    public List<RefundResponse> getMyRefunds() {
        return refundRepository.findByUserId(userContext.getCurrentUserId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<RefundResponse> getAllRefundRequests() {
        return refundRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<RefundResponse> getRefundsByAllocation(Long allocationId) {
        return refundRepository.findByAllocationId(allocationId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public RefundResponse approveRefund(Long id) {
        StudentFeeRefund refund = refundRepository.findById(id).orElseThrow();
        refund.setStatus(StudentFeeRefund.RefundStatus.APPROVED);
        refund.setApprovedBy(userContext.getCurrentUserId());
        refund.setApprovedAt(LocalDateTime.now());
        // In a real system, you'd trigger a gateway refund here if integrated
        return mapToResponse(refundRepository.save(refund));
    }

    @Override
    public RefundResponse rejectRefund(Long id, String reason) {
        StudentFeeRefund refund = refundRepository.findById(id).orElseThrow();
        refund.setStatus(StudentFeeRefund.RefundStatus.REJECTED);
        refund.setRejectedBy(userContext.getCurrentUserId());
        refund.setRejectionReason(reason);
        return mapToResponse(refundRepository.save(refund));
    }

    @Override
    public void deleteRefundRequest(Long id) {
        refundRepository.deleteById(id);
    }

    private RefundResponse mapToResponse(StudentFeeRefund refund) {
        return FeeMapper.toResponse(refund);
    }
}
