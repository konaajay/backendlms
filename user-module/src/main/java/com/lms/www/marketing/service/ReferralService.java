package com.lms.www.marketing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.lms.www.marketing.event.MarketingEvents.PurchaseCompletedEvent;
import com.lms.www.marketing.model.ReferralCode;
import com.lms.www.marketing.model.ReferralUsage;
import com.lms.www.marketing.model.enums.RewardStatus;
import com.lms.www.marketing.model.enums.WalletTransactionType;
import com.lms.www.marketing.repository.ReferralCodeRepository;
import com.lms.www.marketing.repository.ReferralUsageRepository;

@Service("marketingReferralService")
public class ReferralService {

    private static final Logger log = LoggerFactory.getLogger(ReferralService.class);

    @Autowired
    private ReferralCodeRepository referralCodeRepository;

    @Autowired
    private ReferralUsageRepository referralUsageRepository;

    @Autowired
    private WalletService walletService;

    // Generate code using userId + random suffix to prevent guessing
    private String generateCode(Long userId) {
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "REF" + userId + suffix;
    }

    // Get or create referral code
    public ReferralCode getOrCreateReferralCode(Long userId) {
        return referralCodeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    ReferralCode newCode = new ReferralCode();
                    newCode.setUserId(userId);
                    newCode.setCode(generateCode(userId));
                    log.info("Created new referral code for userId {}: {}", userId, newCode.getCode());
                    return referralCodeRepository.save(newCode);
                });
    }

    // When new user signs up with a referral for a specific course
    @Transactional
    public void processReferral(Long refereeId, String referralCode, Long courseId) {
        if (referralCode == null || referralCode.trim().isEmpty()) {
            return;
        }

        log.info("Processing referral code {} for refereeId {} and courseId {}", referralCode, refereeId, courseId);

        Optional<ReferralCode> codeOpt = referralCodeRepository.findByCodeIgnoreCase(referralCode.trim());

        if (codeOpt.isPresent()) {
            ReferralCode rc = codeOpt.get();

            // prevent self referral
            if (rc.getUserId().equals(refereeId)) {
                log.warn("Self-referral attempt by userId {}", refereeId);
                return;
            }

            // Check if referral for this course already exists
            if (referralUsageRepository.findByReferredUserIdAndCourseId(refereeId, courseId).isPresent()) {
                log.info("Referral for refereeId {} and courseId {} already exists", refereeId, courseId);
                return;
            }

            ReferralUsage usage = new ReferralUsage();
            usage.setReferralCode(rc);
            usage.setReferrerUserId(rc.getUserId());
            usage.setReferredUserId(refereeId);
            usage.setCourseId(courseId);
            usage.setRewardStatus(RewardStatus.PENDING);
            usage.setRewardAmount(BigDecimal.ZERO);

            referralUsageRepository.save(usage);
            log.info("Stored referral usage for referrerId {} and refereeId {} for course {}", rc.getUserId(), refereeId, courseId);
        } else {
            log.warn("Invalid referral code used: {}", referralCode);
        }
    }

    // When purchase event happens, check if it was for a referred course
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePurchase(PurchaseCompletedEvent event) {
        processFirstPurchase(event.getUserId(), event.getCourseId());
    }

    // Reward referrer after purchase of the specific referred course
    @Transactional
    public synchronized void processFirstPurchase(Long refereeId, Long courseId) {
        log.info("Attempting to process purchase reward for userId {} and courseId {}", refereeId, courseId);

        referralUsageRepository.findByReferredUserIdAndCourseId(refereeId, courseId)
                .ifPresent(usage -> {
                    if (RewardStatus.PENDING.equals(usage.getRewardStatus())) {
                        BigDecimal reward = BigDecimal.valueOf(50);

                        try {
                            walletService.addCredits(
                                    usage.getReferrerUserId(),
                                    reward,
                                    WalletTransactionType.EARN,
                                    "REFERRAL",
                                    "COURSE_" + courseId,
                                    "Referral reward for course purchase by user " + refereeId
                            );

                            usage.setRewardStatus(RewardStatus.REWARDED);
                            usage.setRewardAmount(reward);
                            referralUsageRepository.save(usage);

                            log.info("Reward of {} given to referrerId {} for user {} joining course Id {}", 
                                    reward, usage.getReferrerUserId(), refereeId, courseId);
                        } catch (Exception e) {
                            log.error("Failed to credit wallet for referral reward: referrerId={}, amount={}", 
                                    usage.getReferrerUserId(), reward, e);
                        }
                    } else {
                        log.info("Referral reward already processed or not pending for user {} and course {}", refereeId, courseId);
                    }
                });
    }

    // Stats for dashboard
    public List<ReferralUsage> getReferralStats(Long userId) {
        return referralUsageRepository.findByReferralCode_UserId(userId);
    }
}