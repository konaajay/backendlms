package com.lms.www.affiliate.service;

import com.lms.www.affiliate.dto.AffiliateLinkDTO;
import com.lms.www.affiliate.entity.Affiliate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service("affiliateReferralService")
public interface ReferralService {
    /**
     * Gets an existing referral code for a student or creates a new one 
     * if they have purchased a course.
     */
    String getOrCreateReferralCode(Long studentId, Long courseId, boolean forceCreate);

    /**
     * Finds an affiliate (student or partner) by their referral code.
     */
    Optional<Affiliate> getReferrerByCode(String code);

    /**
     * Checks if a student is eligible to refer others for a specific course.
     * Criteria: Must have purchased the course first.
     */
    boolean isEligibleToRefer(Long studentId, Long courseId);

    /**
     * Lists courses purchased by a student, along with existing referral links.
     */
    List<com.lms.www.affiliate.dto.PurchasedCourseResponse> getPurchasedCourses(Long studentId);

    /**
     * Filters a list of affiliate links to only those that the student is eligible to refer.
     */
    List<AffiliateLinkDTO> filterLinksByPurchases(Long studentId, List<AffiliateLinkDTO> links);

    // Secure Methods
    List<com.lms.www.affiliate.dto.PurchasedCourseResponse> getPurchasedCoursesSecure();
    String getOrCreateReferralCodeSecure(Long courseId, boolean forceCreate);
}
