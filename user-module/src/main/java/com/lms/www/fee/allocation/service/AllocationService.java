package com.lms.www.fee.allocation.service;

import com.lms.www.fee.dto.*;
import com.lms.www.fee.allocation.entity.StudentFeeAllocation;
import java.util.List;

public interface AllocationService {
    // Admin Actions
    StudentFeeAllocationResponse create(CreateAllocationRequest request);
    List<StudentFeeAllocationResponse> createBulk(BulkAllocationRequest request);
    StudentFeeAllocationResponse update(Long id, UpdateAllocationRequest request);
    void delete(Long id);
    void syncAllStudentInfo();
    
    // View Actions (Admin)
    StudentFeeAllocationResponse getById(Long id);
    List<StudentFeeAllocationResponse> getAllAllocations();
    List<StudentFeeAllocationResponse> getByUser(Long userId);
    List<StudentFeeAllocationResponse> getByBatch(Long batchId);
    List<StudentFeeAllocationResponse> getByParent(Long parentUserId);
    
    // Secure View Actions (Student/Parent)
    StudentFeeAllocationResponse getByIdSecure(Long id);
    List<StudentFeeAllocationResponse> getByUserSecure(Long userId);
    StudentFeeAllocationResponse getLatestSecure(Long userId);
    List<StudentFeeAllocationResponse> getByParentSecure(Long parentUserId);

    // Helpers
    StudentFeeAllocation getFeeAllocationById(Long id);
}
