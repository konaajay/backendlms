package com.lms.www.fee.payment.service;

import com.lms.www.fee.dto.RefundRequest;
import com.lms.www.fee.dto.RefundResponse;
import java.util.List;

public interface RefundService {
    RefundResponse createRefund(RefundRequest request);
    List<RefundResponse> getMyRefunds();
    List<RefundResponse> getAllRefundRequests();
    List<RefundResponse> getRefundsByAllocation(Long allocationId);
    RefundResponse approveRefund(Long id);
    RefundResponse rejectRefund(Long id, String reason);
    void deleteRefundRequest(Long id);
}
