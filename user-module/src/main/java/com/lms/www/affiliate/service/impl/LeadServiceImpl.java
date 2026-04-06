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
import com.lms.www.affiliate.service.LeadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final AffiliateRepository affiliateRepository;
    private final AffiliateLinkRepository linkRepository;
    private final AffiliateLeadRepository leadRepository;
    private final LeadStatusHistoryRepository historyRepository;
    private final LeadNoteRepository leadNoteRepository;

    private final Map<String, LocalDateTime> ipRequestMap = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public AffiliateLead createLead(String name, String mobile, String email,
            Long courseId, Long batchId, String referralCode, String ipAddress) {

        // Rate limiting
        if (ipAddress != null) {
            // Prevention of memory leak
            if (ipRequestMap.size() > 100_000) {
                ipRequestMap.clear();
            }

            LocalDateTime lastRequest = ipRequestMap.get(ipAddress);
            if (lastRequest != null && lastRequest.plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Too many requests. Please try again after a minute.");
            }
            ipRequestMap.put(ipAddress, LocalDateTime.now());
        }

        // Duplicate check: (mobile, batchId)
        Optional<AffiliateLead> existing = leadRepository.findByMobileAndBatchId(mobile, batchId);
        if (existing.isPresent()) {
            throw new IllegalStateException("A lead with this mobile number already exists for this batch.");
        }

        Optional<AffiliateLink> linkOpt = linkRepository.findByReferralCode(referralCode);

        Affiliate affiliate;
        Long finalCourseId = courseId;
        Long finalBatchId = batchId;
        Long linkId = null;

        if (linkOpt.isPresent()) {
            AffiliateLink link = linkOpt.get();
            if (link.getStatus() != AffiliateLink.LinkStatus.ACTIVE) {
                throw new IllegalStateException("This referral link is no longer active.");
            }
            Long affiliateId = link.getAffiliate().getId();
            if (affiliateId == null)
                throw new IllegalStateException("Affiliate ID is null");
            affiliate = affiliateRepository.findById(affiliateId)
                    .orElseThrow(() -> new IllegalArgumentException("Affiliate not found"));
            finalCourseId = link.getCourseId();
            finalBatchId = link.getBatchId();
            linkId = link.getId();
        } else {
            // It might be a direct affiliate code (Student Referral)
            affiliate = affiliateRepository.findByReferralCode(referralCode)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid referral code or link: " + referralCode));

            if (finalCourseId == null || finalBatchId == null) {
                throw new IllegalArgumentException(
                        "Course ID and Batch ID are required for direct affiliate referrals.");
            }
        }

        if (affiliate.getStatus() != AffiliateStatus.ACTIVE) {
            throw new IllegalStateException("Affiliate partner is currently inactive.");
        }

        // Self-referral prevention
        if (email != null && email.equalsIgnoreCase(affiliate.getEmail())) {
            throw new IllegalArgumentException("Self-referral is not allowed.");
        }
        if (mobile != null && mobile.equals(affiliate.getMobile())) {
            throw new IllegalArgumentException("Self-referral is not allowed (mobile match).");
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

        if (lead == null)
            throw new IllegalStateException("Lead object build failed");
        return leadRepository.save(lead);
    }

    @Override
    @Transactional
    public AffiliateLead updateLeadStatus(Long leadId, AffiliateLead.LeadStatus newStatus,
            String changedBy, String reason) {

        AffiliateLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

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
        if (history == null)
            throw new IllegalStateException("History object build failed");
        historyRepository.save(history);

        return lead;
    }

    @Override
    @Transactional
    public LeadNote addLeadNote(Long leadId, String note, String createdBy) {
        LeadNote leadNote = LeadNote.builder()
                .leadId(leadId)
                .note(note)
                .createdBy(createdBy)
                .build();
        if (leadNote == null)
            throw new IllegalStateException("Note object build failed");
        return leadNoteRepository.save(leadNote);
    }

    @Override
    public List<LeadNote> getLeadNotes(Long leadId) {
        return leadNoteRepository.findByLeadIdOrderByCreatedAtDesc(leadId);
    }

    @Override
    public List<AffiliateLead> getAdminLeads() {
        return leadRepository.findAll();
    }

    @Override
    public List<AffiliateLeadDTO> getLeadsByUserId(Long userId) {
        Affiliate affiliate = affiliateRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found for userId: " + userId));
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
                    .append(lead.getAffiliate().getId()).append(",")
                    .append(lead.getBatchId()).append(",")
                    .append(lead.getStatus()).append(",")
                    .append(lead.getLeadSource()).append(",")
                    .append(lead.getCreatedAt()).append("\n");
        }
        return csv.toString();
    }

    @Override
    public AffiliateLead getLeadByStudentId(Long studentId) {
        Optional<AffiliateLead> lead = leadRepository.findByStudentId(studentId);
        return lead.orElse(null);
    }

    @Override
    public AffiliateLead getLeadByEmailAndBatch(String email, Long batchId) {
        return leadRepository.findByEmailAndBatchId(email, batchId).orElse(null);
    }
}
