package com.lms.www.affiliate.service;

import java.util.List;

import com.lms.www.affiliate.dto.AffiliateLeadDTO;
import com.lms.www.affiliate.entity.AffiliateLead;

public interface AffiliateLeadService {
    AffiliateLeadDTO createLead(String name, String mobile, String email,
            Long courseId, Long batchId, String referralCode, String ipAddress);

    AffiliateLeadDTO updateLeadStatus(Long leadId, AffiliateLead.LeadStatus newStatus,
            String changedBy, String reason);

    AffiliateLeadDTO addLeadNote(Long leadId, String note, String createdBy);
    List<com.lms.www.affiliate.dto.LeadNoteDTO> getLeadNotes(Long leadId);

    List<AffiliateLeadDTO> getAdminLeads();

    List<AffiliateLeadDTO> getLeadsByUserId(Long userId);

    List<AffiliateLeadDTO> getLeadsSecure();

    String exportLeadsCsv();
    AffiliateLeadDTO getLeadByStudentId(Long studentId);
    AffiliateLeadDTO getLeadByEmailAndBatch(String email, Long batchId);
    AffiliateLeadDTO convertToStudent(Long leadId, jakarta.servlet.http.HttpServletRequest request);
}
