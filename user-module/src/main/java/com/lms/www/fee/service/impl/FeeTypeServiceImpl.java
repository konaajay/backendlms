package com.lms.www.fee.service.impl;

import com.lms.www.fee.dto.FeeMapper;
import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import com.lms.www.fee.structure.entity.FeeType;
import com.lms.www.fee.structure.repository.FeeTypeRepository;
import com.lms.www.fee.service.FeeTypeService;
import com.lms.www.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FeeTypeServiceImpl implements FeeTypeService {

    private final FeeTypeRepository repo;

    @Override
    public FeeTypeResponse create(FeeTypeRequest request) {
        FeeType entity = FeeType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive() != null ? request.getActive() : true)
                .mandatory(request.getMandatory() != null ? request.getMandatory() : true)
                .refundable(request.getRefundable() != null ? request.getRefundable() : false)
                .oneTime(request.getOneTime() != null ? request.getOneTime() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
        return FeeMapper.toResponse(repo.save(entity));
    }

    @Override
    public FeeTypeResponse getById(Long id) {
        return FeeMapper.toResponse(repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Type not found: " + id)));
    }

    @Override
    public List<FeeTypeResponse> getAll() {
        return repo.findAll().stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeeTypeResponse> getActive() {
        return repo.findByActiveTrue().stream()
                .map(FeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeeTypeResponse update(Long id, FeeTypeRequest request) {
        FeeType existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fee Type not found: " + id));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setActive(request.getActive() != null ? request.getActive() : existing.getActive());
        existing.setMandatory(request.getMandatory() != null ? request.getMandatory() : existing.getMandatory());
        existing.setRefundable(request.getRefundable() != null ? request.getRefundable() : existing.getRefundable());
        existing.setOneTime(request.getOneTime() != null ? request.getOneTime() : existing.getOneTime());
        existing.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : existing.getDisplayOrder());
        return FeeMapper.toResponse(repo.save(existing));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}