package com.lms.www.marketing.repository;

import com.lms.www.marketing.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {
    List<PushSubscription> findByLearnerId(Long learnerId);

    List<PushSubscription> findByPlatform(String platform);
}
