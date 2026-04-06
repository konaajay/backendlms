package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.ReferralUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReferralUsageRepository extends JpaRepository<ReferralUsage, Long> {
    List<ReferralUsage> findByReferralCode_UserId(Long userId);

    Optional<ReferralUsage> findByReferredUserId(Long referredUserId);

    Optional<ReferralUsage> findByReferredUserIdAndCourseId(Long referredUserId, Long courseId);
}
