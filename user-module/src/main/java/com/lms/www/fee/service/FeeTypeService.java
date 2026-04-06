package com.lms.www.fee.service;

import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import java.util.List;

public interface FeeTypeService {
    FeeTypeResponse create(FeeTypeRequest request);
    FeeTypeResponse getById(Long id);
    List<FeeTypeResponse> getAll();
    List<FeeTypeResponse> getActive();
    FeeTypeResponse update(Long id, FeeTypeRequest request);
    void delete(Long id);
}