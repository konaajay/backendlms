package com.lms.www.fee.structure.service;

import com.lms.www.fee.dto.FeeStructureRequest;
import com.lms.www.fee.dto.FeeStructureResponse;
import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import java.util.List;

public interface StructureService {
    FeeStructureResponse createStructure(FeeStructureRequest request);
    FeeStructureResponse getStructureById(Long id);
    List<FeeStructureResponse> getStructuresByCourse(Long courseId);
    List<FeeStructureResponse> getStructuresByBatch(Long batchId);
    List<FeeStructureResponse> getAllStructures();
    FeeStructureResponse updateStructure(Long id, FeeStructureRequest request);
    void deleteStructure(Long id);
    
    FeeTypeResponse createFeeType(FeeTypeRequest request);
    List<FeeTypeResponse> getAllFeeTypes();
    FeeTypeResponse updateFeeType(Long id, FeeTypeRequest request);
    void deleteFeeType(Long id);
}
