package com.lms.www.fee.structure.service;

import com.lms.www.fee.dto.FeeStructureRequest;
import com.lms.www.fee.dto.FeeStructureResponse;
import com.lms.www.fee.dto.FeeTypeRequest;
import com.lms.www.fee.dto.FeeTypeResponse;
import com.lms.www.fee.structure.entity.FeeStructure;
import com.lms.www.fee.structure.entity.FeeStructureComponent;
import com.lms.www.fee.structure.entity.FeeType;
import com.lms.www.fee.structure.repository.FeeStructureComponentRepository;
import com.lms.www.fee.structure.repository.FeeStructureRepository;
import com.lms.www.fee.structure.repository.FeeTypeRepository;
import com.lms.www.fee.penalty.entity.FeePenaltySlab;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.repository.BatchRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StructureServiceImpl implements StructureService {

    private final FeeStructureRepository structureRepository;
    private final FeeStructureComponentRepository componentRepository;
    private final FeeTypeRepository feeTypeRepository;
    private final BatchRepository batchRepository;

    @Override
    public FeeStructureResponse createStructure(FeeStructureRequest request) {
        log.info("Creating fee structure: {}", request.getName());
        
        FeeStructure structure = new FeeStructure();
        structure.setName(request.getName());
        structure.setAcademicYear(request.getAcademicYear());
        
        Long courseId = request.getCourseId();
        if (courseId == null && request.getBatchId() != null) {
            courseId = batchRepository.findById(request.getBatchId())
                    .map(b -> b.getCourseId())
                    .orElse(null);
        }
        structure.setCourseId(courseId);
        structure.setBatchId(request.getBatchId());
        
        structure.setDurationMonths(request.getDurationMonths());
        structure.setBaseAmount(request.getBaseAmount());
        structure.setTotalAmount(request.getTotalAmount());
        structure.setInstallmentCount(request.getInstallmentCount());
        structure.setGstApplicable(request.getGstApplicable());
        structure.setGstPercent(request.getGstPercent());
        
        // Map new fields
        structure.setCourseName(request.getCourseName());
        structure.setBatchName(request.getBatchName());
        structure.setFeeTypeId(request.getFeeTypeId());
        structure.setAdmissionFeeAmount(request.getAdmissionFeeAmount());
        structure.setDiscountType(request.getDiscountType());
        structure.setDiscountValue(request.getDiscountValue());
        structure.setGstIncludedInFee(request.getGstIncludedInFee() != null ? request.getGstIncludedInFee() : false);
        structure.setPenaltyType(request.getPenaltyType());
        structure.setFixedPenaltyAmount(request.getFixedPenaltyAmount());
        structure.setPenaltyPercentage(request.getPenaltyPercentage());
        structure.setMaxPenaltyCap(request.getMaxPenaltyCap());
        structure.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        structure = structureRepository.save(structure);

        // Process Penalty Slabs
        if (request.getSlabs() != null) {
            for (var slabReq : request.getSlabs()) {
                FeePenaltySlab slab = new FeePenaltySlab();
                slab.setFeeStructure(structure);
                slab.setFromDay(slabReq.getFromDay());
                slab.setToDay(slabReq.getToDay());
                slab.setPenaltyType(slabReq.getPenaltyType());
                slab.setValue(slabReq.getValue());
                slab.setActive(true);
                structure.addSlab(slab);
            }
        }

        // Process Breakdown items
        if (request.getBreakdown() != null) {
            for (var itemReq : request.getBreakdown()) {
                FeeStructureComponent component = new FeeStructureComponent();
                component.setFeeStructure(structure);
                component.setName(itemReq.getName());
                component.setAmount(itemReq.getAmount());
                component.setFeeTypeId(itemReq.getFeeTypeId());
                component.setRefundable(itemReq.getRefundable() != null ? itemReq.getRefundable() : true);
                component.setIsRefundable(component.getRefundable());
                component.setMandatory(itemReq.getMandatory() != null ? itemReq.getMandatory() : true);
                component.setIsMandatory(component.getMandatory());
                component.setDueDate(null); 
                structure.addComponent(component);
            }
        }

        // Process Installments
        if (request.getComponents() != null) {
            for (var compReq : request.getComponents()) {
                FeeStructureComponent component = new FeeStructureComponent();
                component.setFeeStructure(structure);
                component.setName(compReq.getName());
                component.setAmount(compReq.getAmount());
                component.setFeeTypeId(compReq.getFeeTypeId());
                component.setDueDate(compReq.getDueDate());
                component.setInstallmentAllowed(true);
                structure.addComponent(component);
            }
        }
        
        structure = structureRepository.save(structure);
        return mapToResponse(structure);
    }

    @Override
    public FeeStructureResponse getStructureById(Long id) {
        return structureRepository.findById(id).map(this::mapToResponse).orElseThrow();
    }

    @Override
    public List<FeeStructureResponse> getStructuresByCourse(Long courseId) {
        return structureRepository.findByCourseId(courseId).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FeeStructureResponse> getStructuresByBatch(Long batchId) {
        log.info("Fetching fee structures for batch: {}", batchId);
        List<FeeStructure> structures = new ArrayList<>(structureRepository.findByBatchIdAndIsActiveTrue(batchId));
        
        if (structures.isEmpty()) {
            log.info("No specific fee structure found for batch {}, checking for course-level fallback", batchId);
            batchRepository.findById(batchId).ifPresent(batch -> {
                if (batch.getCourseId() != null) {
                    List<FeeStructure> courseStructures = structureRepository.findByCourseIdAndBatchIdIsNullAndIsActiveTrue(batch.getCourseId());
                    structures.addAll(courseStructures);
                }
            });
        }
        
        return structures.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<FeeStructureResponse> getAllStructures() {
        return structureRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public FeeStructureResponse updateStructure(Long id, FeeStructureRequest request) {
        log.info("Updating fee structure ID: {}", id);
        FeeStructure structure = structureRepository.findById(id).orElseThrow();
        structure.setName(request.getName());
        structure.setAcademicYear(request.getAcademicYear());
        structure.setBaseAmount(request.getBaseAmount());
        structure.setTotalAmount(request.getTotalAmount());
        structure.setDurationMonths(request.getDurationMonths());
        structure.setInstallmentCount(request.getInstallmentCount());

        // Update new fields
        structure.setCourseId(request.getCourseId());
        structure.setBatchId(request.getBatchId());
        structure.setCourseName(request.getCourseName());
        structure.setBatchName(request.getBatchName());
        structure.setFeeTypeId(request.getFeeTypeId());
        structure.setAdmissionFeeAmount(request.getAdmissionFeeAmount());
        structure.setDiscountType(request.getDiscountType());
        structure.setDiscountValue(request.getDiscountValue());
        structure.setGstIncludedInFee(request.getGstIncludedInFee() != null ? request.getGstIncludedInFee() : false);
        structure.setPenaltyType(request.getPenaltyType());
        structure.setFixedPenaltyAmount(request.getFixedPenaltyAmount());
        structure.setPenaltyPercentage(request.getPenaltyPercentage());
        structure.setMaxPenaltyCap(request.getMaxPenaltyCap());
        structure.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        // Clear existing components & slabs
        structure.getComponents().clear();
        structure.getSlabs().clear();

        // Process Penalty Slabs
        if (request.getSlabs() != null) {
            for (var slabReq : request.getSlabs()) {
                FeePenaltySlab slab = new FeePenaltySlab();
                slab.setFeeStructure(structure);
                slab.setFromDay(slabReq.getFromDay());
                slab.setToDay(slabReq.getToDay());
                slab.setPenaltyType(slabReq.getPenaltyType());
                slab.setValue(slabReq.getValue());
                slab.setActive(true);
                structure.addSlab(slab);
            }
        }
        
        // Re-add Breakdown
        if (request.getBreakdown() != null) {
            for (var itemReq : request.getBreakdown()) {
                FeeStructureComponent c = new FeeStructureComponent();
                c.setFeeStructure(structure);
                c.setName(itemReq.getName());
                c.setAmount(itemReq.getAmount());
                c.setFeeTypeId(itemReq.getFeeTypeId());
                c.setRefundable(itemReq.getRefundable() != null ? itemReq.getRefundable() : true);
                c.setIsRefundable(c.getRefundable());
                c.setDueDate(null);
                structure.addComponent(c);
            }
        }

        // Re-add Installments
        if (request.getComponents() != null) {
            for (var compReq : request.getComponents()) {
                FeeStructureComponent c = new FeeStructureComponent();
                c.setFeeStructure(structure);
                c.setName(compReq.getName());
                c.setAmount(compReq.getAmount());
                c.setDueDate(compReq.getDueDate());
                structure.addComponent(c);
            }
        }

        return mapToResponse(structureRepository.save(structure));
    }

    @Override
    public void deleteStructure(Long id) {
        structureRepository.deleteById(id);
    }

    @Override
    public FeeTypeResponse createFeeType(FeeTypeRequest request) {
        FeeType type = FeeType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
        return mapToResponse(feeTypeRepository.save(type));
    }

    @Override
    public List<FeeTypeResponse> getAllFeeTypes() {
        return feeTypeRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public FeeTypeResponse updateFeeType(Long id, FeeTypeRequest request) {
        FeeType type = feeTypeRepository.findById(id).orElseThrow();
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setActive(request.getActive());
        return mapToResponse(feeTypeRepository.save(type));
    }

    @Override
    public void deleteFeeType(Long id) {
        feeTypeRepository.deleteById(id);
    }

    private FeeStructureResponse mapToResponse(FeeStructure structure) {
        if (structure == null) return null;
        return FeeStructureResponse.builder()
                .id(structure.getId())
                .name(structure.getName())
                .academicYear(structure.getAcademicYear())
                .courseId(structure.getCourseId())
                .courseName(structure.getCourseName())
                .batchId(structure.getBatchId())
                .batchName(structure.getBatchName())
                .totalAmount(structure.getTotalAmount())
                .baseAmount(structure.getBaseAmount())
                .currency(structure.getCurrency())
                .isActive(structure.getIsActive())
                .admissionFeeAmount(structure.getAdmissionFeeAmount())
                .discountType(structure.getDiscountType())
                .discountValue(structure.getDiscountValue())
                .gstApplicable(structure.getGstApplicable())
                .gstPercent(structure.getGstPercent())
                .penaltyType(structure.getPenaltyType())
                .fixedPenaltyAmount(structure.getFixedPenaltyAmount())
                .penaltyPercentage(structure.getPenaltyPercentage())
                .maxPenaltyCap(structure.getMaxPenaltyCap())
                .installmentCount(structure.getInstallmentCount())
                .durationMonths(structure.getDurationMonths())
                .slabs(structure.getSlabs() != null ? structure.getSlabs().stream()
                        .map(s -> FeeStructureResponse.SlabDTO.builder()
                                .id(s.getId())
                                .fromDay(s.getFromDay())
                                .toDay(s.getToDay())
                                .type(s.getPenaltyType() != null ? s.getPenaltyType().name() : null)
                                .value(s.getValue())
                                .build())
                        .collect(Collectors.toList()) : null)
                .components(structure.getComponents() != null ? structure.getComponents().stream()
                        .map(c -> FeeStructureResponse.ComponentDTO.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .amount(c.getAmount())
                                .feeTypeId(c.getFeeTypeId())
                                .dueDate(c.getDueDate())
                                .refundable(c.getIsRefundable())
                                .mandatory(c.getIsMandatory())
                                .installmentAllowed(c.getInstallmentAllowed())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private FeeTypeResponse mapToResponse(FeeType type) {
        if (type == null) return null;
        return FeeTypeResponse.builder()
                .id(type.getId())
                .name(type.getName())
                .description(type.getDescription())
                .active(type.getActive())
                .build();
    }
}
