package com.lms.www.affiliate.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.entity.AffiliateLead;
import com.lms.www.affiliate.entity.LeadNote;

@Service("affiliateLeadService")
public interface LeadService {
    AffiliateLead createLead(String name, String mobile, String email,
            Long courseId, Long batchId, String referralCode, String ipAddress);

    AffiliateLead updateLeadStatus(Long leadId, AffiliateLead.LeadStatus newStatus,
            String changedBy, String reason);

    LeadNote addLeadNote(Long leadId, String note, String createdBy);

    List<LeadNote> getLeadNotes(Long leadId);

    List<AffiliateLead> getAdminLeads();

    List<AffiliateLeadDTO> getLeadsByUserId(Long userId);

    String exportLeadsCsv();
    AffiliateLead getLeadByStudentId(Long studentId);
    AffiliateLead getLeadByEmailAndBatch(String email, Long batchId);
}
