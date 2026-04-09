package com.lms.www.affiliate.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateStatus;
import com.lms.www.affiliate.entity.AffiliateLead;
import com.lms.www.affiliate.entity.AffiliateLink;
import com.lms.www.affiliate.entity.LeadNote;
import com.lms.www.affiliate.entity.LeadStatusHistory;
import com.lms.www.affiliate.repository.AffiliateLeadRepository;
import com.lms.www.affiliate.repository.AffiliateLinkRepository;
import com.lms.www.affiliate.repository.AffiliateRepository;
import com.lms.www.affiliate.repository.LeadNoteRepository;
import com.lms.www.affiliate.repository.LeadStatusHistoryRepository;
import com.lms.www.affiliate.service.AffiliateLeadService;
import com.lms.www.community.service.CommunityService;
import com.lms.www.controller.request.StudentRequest;
import com.lms.www.model.Student;
import com.lms.www.model.User;
import com.lms.www.management.util.SecurityUtil;
import com.lms.www.service.AdminService;
import com.lms.www.management.model.Batch;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.repository.BatchRepository;
import com.lms.www.management.service.StudentBatchService;
import com.lms.www.affiliate.entity.AffiliateSale;
import com.lms.www.affiliate.repository.AffiliateSaleRepository;
import com.lms.www.repository.StudentRepository;
import com.lms.www.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import com.lms.www.fee.service.StudentFeeAllocationService;
import com.lms.www.fee.structure.service.StructureService;
import com.lms.www.fee.dto.CreateAllocationRequest;
import com.lms.www.fee.dto.FeeStructureResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AffiliateLeadServiceImpl implements AffiliateLeadService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateLinkRepository linkRepository;
    private final AffiliateLeadRepository leadRepository;
    private final LeadStatusHistoryRepository historyRepository;
    private final LeadNoteRepository leadNoteRepository;
    private final SecurityUtil securityUtil;
    private final CommunityService communityService;
    private final AffiliateSaleRepository affiliateSaleRepository;
    private final AdminService adminService;
    private final BatchRepository batchRepository;
    private final StudentBatchService studentBatchService;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    
    private final ObjectProvider<StudentFeeAllocationService> allocationServiceProvider;
    private final ObjectProvider<StructureService> structureServiceProvider;

    private final Map<String, LocalDateTime> ipRequestMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public AffiliateLeadDTO createLead(String name, String mobile, String email,
            Long courseId, Long batchId, String referralCode, String ipAddress) {
        // ... (Rate limiting and validation logic remains same, but return mapped DTO)
        // I will keep the logic same but wrap the save result in mapToDTO

        // Rate limiting
        if (ipAddress != null) {
            LocalDateTime lastRequest = ipRequestMap.get(ipAddress);
            if (lastRequest != null && lastRequest.plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Too many requests. Please try again after a minute.");
            }
            ipRequestMap.put(ipAddress, LocalDateTime.now());
        }

        Optional<AffiliateLead> existing = leadRepository.findByMobileAndBatchId(mobile, batchId);
        if (existing.isPresent()) {
            log.warn("[LeadService] DUPLICATE LEAD: Mobile {} for batch {} already assigned to affiliate ID: {}", 
                mobile, batchId, existing.get().getAffiliate() != null ? existing.get().getAffiliate().getId() : "NONE");
            return mapToDTO(existing.get());
        }

        Optional<AffiliateLink> linkOpt = linkRepository.findByReferralCode(referralCode);

        Affiliate affiliate;
        Long finalCourseId = courseId;
        Long finalBatchId = batchId;
        Long linkId = null;

        if (linkOpt.isPresent()) {
            AffiliateLink link = linkOpt.get();
            if (link.getStatus() != AffiliateLink.LinkStatus.ACTIVE) {
                throw new RuntimeException("This referral link is no longer active.");
            }
            Long affiliateId = link.getAffiliate().getId();
            affiliate = affiliateRepository.findById(affiliateId)
                    .orElseThrow(() -> new RuntimeException("Affiliate not found"));
            finalCourseId = link.getCourseId();
            finalBatchId = link.getBatchId();
            linkId = link.getId();
        } else {
            log.info("[LeadService] Looking up affiliate for referralCode: {}", referralCode);
            affiliate = affiliateRepository.findByReferralCode(referralCode)
                    .orElseThrow(() -> {
                        log.error("[LeadService] REJECTED: Invalid referral code {}", referralCode);
                        return new RuntimeException("Invalid referral code or link: " + referralCode);
                    });

            if (finalCourseId == null || finalBatchId == null) {
                log.error("[LeadService] REJECTED: Missing course/batch for code {}", referralCode);
                throw new RuntimeException("Course ID and Batch ID are required for direct affiliate referrals.");
            }
        }

        log.info("[LeadService] Attributing lead to Affiliate ID: {}, Name: {}, Code: {}", 
            affiliate.getId(), affiliate.getName(), affiliate.getReferralCode());

        if (affiliate.getStatus() != AffiliateStatus.ACTIVE) {
            throw new RuntimeException("Affiliate partner is currently inactive.");
        }

        if (email != null && email.equalsIgnoreCase(affiliate.getEmail())) {
            throw new RuntimeException("Self-referral is not allowed.");
        }

        AffiliateLead lead = AffiliateLead.builder()
                .name(name)
                .mobile(mobile)
                .email(email)
                .courseId(finalCourseId)
                .batchId(finalBatchId)
                .affiliate(affiliate)
                .linkId(linkId)
                .status(AffiliateLead.LeadStatus.NEW)
                .leadSource("AFFILIATE")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        AffiliateLead savedLead = leadRepository.save(lead);
        log.info("[LeadService] SUCCESSFULLY Saved Lead ID: {} for affiliate: {}", savedLead.getId(), affiliate.getName());

        // Trigger community join
        try {
            communityService.addLeadToCommunity(savedLead.getId(), savedLead.getCourseId(), savedLead.getBatchId());
        } catch (Exception e) {
            log.error("Failed to add affiliate lead to community: {}", e.getMessage());
        }

        return mapToDTO(savedLead);
    }

    @Override
    @Transactional
    public AffiliateLeadDTO updateLeadStatus(Long leadId, AffiliateLead.LeadStatus newStatus,
            String changedBy, String reason) {

        AffiliateLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        String oldStatus = (lead.getStatus() != null) ? lead.getStatus().name() : "NONE";
        lead.setStatus(newStatus);

        if (reason != null && !reason.trim().isEmpty()) {
            lead.setRejectionReason(reason);
        }

        leadRepository.save(lead);

        LeadStatusHistory history = LeadStatusHistory.builder()
                .leadId(leadId)
                .oldStatus(oldStatus)
                .newStatus(newStatus.name())
                .changedBy(changedBy != null ? changedBy : "SYSTEM")
                .notes(reason)
                .build();
        historyRepository.save(history);

        return mapToDTO(lead);
    }

    @Override
    @Transactional
    public AffiliateLeadDTO addLeadNote(Long leadId, String note, String createdBy) {
        LeadNote leadNote = LeadNote.builder()
                .leadId(leadId)
                .note(note)
                .createdBy(createdBy)
                .build();
        leadNoteRepository.save(leadNote);

        return leadRepository.findById(leadId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
    }

    @Override
    public List<com.lms.www.affiliate.dto.LeadNoteDTO> getLeadNotes(Long leadId) {
        return leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(leadId).stream()
                .map(n -> com.lms.www.affiliate.dto.LeadNoteDTO.builder()
                        .id(n.getId())
                        .leadId(n.getLeadId())
                        .note(n.getNote())
                        .createdBy(n.getCreatedBy())
                        .createdAt(n.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliateLeadDTO> getAdminLeads() {
        return leadRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliateLeadDTO> getLeadsByUserId(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Affiliate not found for userId: " + userId));
        return leadRepository.findByAffiliate(affiliate).stream()
                .map(l -> AffiliateLeadDTO.builder()
                        .id(l.getId())
                        .name(l.getName())
                        .mobile(l.getMobile())
                        .email(l.getEmail())
                        .courseId(l.getCourseId())
                        .batchId(l.getBatchId())
                        .affiliateId(l.getAffiliate() != null ? l.getAffiliate().getId() : null)
                        .affiliateName(l.getAffiliate() != null ? l.getAffiliate().getName() : null)
                        .referralCode(l.getAffiliate() != null ? l.getAffiliate().getReferralCode() : null)
                        .linkId(l.getLinkId())
                        .status(l.getStatus() != null ? l.getStatus().name() : null)
                        .leadSource(l.getLeadSource())
                        .createdAt(l.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<AffiliateLeadDTO> getLeadsSecure() {
        Long userId = securityUtil.getUserId();
        return getLeadsByUserId(userId);
    }

    @Override
    public String exportLeadsCsv() {
        List<AffiliateLead> leads = leadRepository.findAll();
        StringBuilder csv = new StringBuilder("ID,Name,Email,Mobile,AffiliateID,BatchID,Status,Source,CreatedAt\n");
        for (AffiliateLead lead : leads) {
            csv.append(lead.getId()).append(",")
                    .append("\"").append(lead.getName() != null ? lead.getName().replace("\"", "\"\"") : "")
                    .append("\",")
                    .append("\"").append(lead.getEmail() != null ? lead.getEmail().replace("\"", "\"\"") : "")
                    .append("\",")
                    .append(lead.getMobile()).append(",")
                    .append(lead.getAffiliate() != null ? lead.getAffiliate().getId() : "").append(",")
                    .append(lead.getBatchId()).append(",")
                    .append(lead.getStatus() != null ? lead.getStatus().name() : "").append(",")
                    .append(lead.getLeadSource()).append(",")
                    .append(lead.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }

    @Override
    public AffiliateLeadDTO getLeadByStudentId(Long studentId) {
        return leadRepository.findByStudentId(studentId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public AffiliateLeadDTO getLeadByEmailAndBatch(String email, Long batchId) {
        return leadRepository.findByEmailAndBatchId(email, batchId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public AffiliateLeadDTO updateLead(Long leadId, AffiliateLeadDTO request) {
        AffiliateLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        lead.setName(request.getName());
        lead.setEmail(request.getEmail());
        lead.setMobile(request.getMobile());
        
        if (request.getStatus() != null) {
            lead.setStatus(AffiliateLead.LeadStatus.valueOf(request.getStatus()));
        }

        AffiliateLead saved = leadRepository.save(lead);
        return mapToDTO(saved);
    }
    
    @Override
    @Transactional
    public AffiliateLeadDTO convertToStudent(Long leadId, HttpServletRequest httpRequest) {
        AffiliateLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found: " + leadId));

        if (lead.getStatus() == AffiliateLead.LeadStatus.ENROLLED) {
            throw new RuntimeException("Lead is already enrolled.");
        }

        // 1. Fetch related data
        Batch batch = batchRepository.findById(lead.getBatchId())
                .orElseThrow(() -> new RuntimeException("Batch not found for ID: " + lead.getBatchId()));
                
        // Robust user ID extraction
        Long adminIdVal;
        try {
            adminIdVal = securityUtil.getUserId();
        } catch (Exception e) {
            // Fallback for context issues
            adminIdVal = null;
        }
        final Long adminId = adminIdVal;

        User requestAdminUser;
        if (adminId != null) {
            requestAdminUser = userRepository.findById(adminId)
                    .orElseThrow(() -> new RuntimeException("Admin context not found (ID: " + adminId + ")"));
        } else {
            // If ID extraction fails, try by email which is usually standard in Authentication
            String adminEmail = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
            requestAdminUser = userRepository.findByEmail(adminEmail)
                    .orElseThrow(() -> new RuntimeException("Admin context not found (Email: " + adminEmail + ")"));
        }
        
        // 2. Prepare Student Request
        StudentRequest studentReq = new StudentRequest();
        String[] nameParts = lead.getName().split(" ", 2);
        studentReq.setFirstName(nameParts[0]);
        studentReq.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        studentReq.setEmail(lead.getEmail());
        studentReq.setPhone(lead.getMobile());
        studentReq.setRoleName("ROLE_STUDENT");
        studentReq.setDob(LocalDate.of(2000, 1, 1)); // Default for now
        studentReq.setGender("Other"); // Default
        
        // Generate secure random password
        String rawPassword = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        studentReq.setPassword(rawPassword);

        // 3. Create Student (AdminService handles email dispatch)
        adminService.createStudent(studentReq, requestAdminUser, httpRequest);

        // Fetch newly created Student
        User newUser = userRepository.findByEmail(lead.getEmail())
                .orElseThrow(() -> new RuntimeException("User creation failed."));
        Student student = studentRepository.findByUser_UserId(newUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Student creation failed."));

        // 4. Try Automatic Fee Allocation and Enrollment
        boolean feeAllocated = false;
        try {
            StructureService structureService = structureServiceProvider.getIfAvailable();
            StudentFeeAllocationService allocationService = allocationServiceProvider.getIfAvailable();
            
            if (structureService != null && allocationService != null) {
                // Find potential fee structures for this batch
                List<FeeStructureResponse> structures = structureService.getStructuresByBatch(batch.getBatchId());
                if (!structures.isEmpty()) {
                    FeeStructureResponse structure = structures.get(0); // Take first active structure
                    
                    CreateAllocationRequest allocReq = new CreateAllocationRequest();
                    allocReq.setUserId(newUser.getUserId());
                    allocReq.setFeeStructureId(structure.getId());
                    allocReq.setAdminDiscount(BigDecimal.ZERO); // Default
                    allocReq.setAdditionalDiscount(BigDecimal.ZERO);
                    allocReq.setAffiliateDiscount(null); // Force auto-calculation from Lead
                    
                    // Trigger Full Fee Engine: This handles Allocation, Enrollment, and Sale/Commission
                    allocationService.create(allocReq);
                    feeAllocated = true;
                    log.info("[LeadConversion] Successfully automated fee allocation for student {}", newUser.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("[LeadConversion] Automatic fee allocation failed, falling back to manual: {}", e.getMessage());
        }

        // 5. Fallback Manual Logic if Fee Service is absent or failed
        if (!feeAllocated) {
            // 5a. Enroll Student in Batch
            StudentBatch sb = new StudentBatch();
            sb.setStudentId(student.getStudentId());
            sb.setStudentName(lead.getName());
            sb.setStudentEmail(lead.getEmail());
            sb.setBatchId(batch.getBatchId());
            sb.setCourseId(batch.getCourseId());
            sb.setStatus("ACTIVE");
            sb.setJoinedAt(LocalDateTime.now());
            sb.setUserId(newUser.getUserId());
            studentBatchService.enrollStudent(sb);

            // 5b. Calculate and Record Commission
            Affiliate affiliate = lead.getAffiliate();
            
            BigDecimal originalAmount = batch.getFee() != null ? BigDecimal.valueOf(batch.getFee()) : BigDecimal.ZERO;
            BigDecimal discountAmount = affiliate.getStudentDiscountValue() != null ? affiliate.getStudentDiscountValue() : BigDecimal.ZERO;
            BigDecimal orderAmount = originalAmount.subtract(discountAmount);
            if (orderAmount.compareTo(BigDecimal.ZERO) < 0) orderAmount = BigDecimal.ZERO;

            BigDecimal commissionAmount = BigDecimal.ZERO;
            if (affiliate.getCommissionValue() != null && affiliate.getCommissionValue().compareTo(BigDecimal.ZERO) > 0) {
                commissionAmount = orderAmount.multiply(affiliate.getCommissionValue()).divide(BigDecimal.valueOf(100));
            }

            AffiliateSale sale = AffiliateSale.builder()
                    .affiliate(affiliate)
                    .courseId(batch.getCourseId())
                    .batchId(batch.getBatchId())
                    .orderId("ORD-LEAD-" + lead.getId() + "-" + System.currentTimeMillis())
                    .leadId(lead.getId())
                    .studentId(student.getStudentId())
                    .originalAmount(originalAmount)
                    .discountAmount(discountAmount)
                    .orderAmount(orderAmount)
                    .commissionAmount(commissionAmount)
                    .status(AffiliateSale.SaleStatus.APPROVED) // Auto approve for now
                    .build();
                    
            affiliateSaleRepository.save(sale);

            // 5c. Update Lead Status
            lead.setStatus(AffiliateLead.LeadStatus.ENROLLED);
            leadRepository.save(lead);

            LeadStatusHistory history = LeadStatusHistory.builder()
                    .leadId(leadId)
                    .oldStatus("NEW") // Assuming it was NEW or something else
                    .newStatus(AffiliateLead.LeadStatus.ENROLLED.name())
                    .changedBy(requestAdminUser.getEmail())
                    .notes("Auto converted to student (Manual Fallback)")
                    .build();
            historyRepository.save(history);
        }

        return mapToDTO(lead);
    }

    private AffiliateLeadDTO mapToDTO(AffiliateLead l) {
        return AffiliateLeadDTO.builder()
                .id(l.getId())
                .name(l.getName())
                .mobile(l.getMobile())
                .email(l.getEmail())
                .courseId(l.getCourseId())
                .batchId(l.getBatchId())
                .affiliateId(l.getAffiliate() != null ? l.getAffiliate().getId() : null)
                .affiliateName(l.getAffiliate() != null ? l.getAffiliate().getName() : null)
                .referralCode(l.getAffiliate() != null ? l.getAffiliate().getReferralCode() : null)
                .linkId(l.getLinkId())
                .status(l.getStatus() != null ? l.getStatus().name() : null)
                .leadSource(l.getLeadSource())
                .ipAddress(l.getIpAddress())
                .createdAt(l.getCreatedAt())
                .studentDiscountValue(l.getAffiliate() != null ? l.getAffiliate().getStudentDiscountValue() : null)
                .build();
    }
}
