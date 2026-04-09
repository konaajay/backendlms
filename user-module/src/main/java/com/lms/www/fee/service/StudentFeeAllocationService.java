package com.lms.www.fee.service;

import com.lms.www.fee.dto.*;
import java.util.List;

public interface StudentFeeAllocationService {

    StudentFeeAllocationResponse create(CreateAllocationRequest request);

    List<StudentFeeAllocationResponse> createBulk(BulkAllocationRequest request);

    StudentFeeAllocationResponse getById(Long id);

    StudentFeeAllocationResponse getByIdSecure(Long id);

    List<StudentFeeAllocationResponse> getByUser(Long userId);

    List<StudentFeeAllocationResponse> getByUserSecure(Long userId);

    StudentFeeAllocationResponse getLatest(Long userId);

    StudentFeeAllocationResponse getLatestSecure(Long userId);

    List<StudentFeeAllocationResponse> getByBatch(Long batchId);

    List<StudentFeeAllocationResponse> getAllAllocations();

    StudentFeeAllocationResponse update(Long id, UpdateAllocationRequest request);

    void delete(Long id);

    List<StudentFeeAllocationResponse> getByParent(Long parentUserId);

    List<StudentFeeAllocationResponse> getByParentSecure(Long parentUserId);

    void syncStudentInfo(Long id);

    void syncAllStudentInfo();

    // Internal usage or helper if needed
    com.lms.www.fee.allocation.entity.StudentFeeAllocation getFeeAllocationById(Long id);

    com.lms.www.fee.allocation.entity.StudentFeeAllocation getFeeAllocationByUserId(Long userId);

    StudentLedgerResponse getStudentLedger(Long userId);
}
