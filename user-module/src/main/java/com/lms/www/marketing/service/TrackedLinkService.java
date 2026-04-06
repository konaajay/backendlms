package com.lms.www.marketing.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lms.www.marketing.dto.TrackedLinkAnalyticsDTO;
import com.lms.www.marketing.model.TrackedLink;
import com.lms.www.marketing.repository.LeadRepository;
import com.lms.www.marketing.repository.TrackedLinkRepository;
import com.lms.www.marketing.repository.MarketingTrafficEventRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TrackedLinkService {

    private static final Logger log = LoggerFactory.getLogger(TrackedLinkService.class);

    @Autowired
    private TrackedLinkRepository repository;

    @Autowired
    private MarketingTrafficEventRepository trafficEventRepository;

    @Autowired
    private LeadRepository leadRepository;

    public TrackedLink saveLink(TrackedLink link) {
        return repository.save(link);
    }

    public List<TrackedLink> getAllLinks() {
        return repository.findAllByOrderByTimestampDesc();
    }

    public List<TrackedLinkAnalyticsDTO> getAllLinksWithAnalytics() {
        List<TrackedLink> links = repository.findAllByOrderByTimestampDesc();
        log.info("Aggregating analytics for {} tracked links", links.size());
        return links.stream().map(link -> {
            // Standardize: Look for events matching either source/campaign OR utmSource/utmCampaign
            // Clicks in global dashboard standard = PAGE_VIEW + CLICK events
            long clicks = trafficEventRepository.countByUtmSourceAndUtmCampaignAndEventType(
                    link.getSource(), link.getCampaign(), "CLICK");
            long views = trafficEventRepository.countByUtmSourceAndUtmCampaignAndEventType(
                    link.getSource(), link.getCampaign(), "PAGE_VIEW");
            long signups = leadRepository.countByUtmSourceAndUtmCampaign(
                    link.getSource(), link.getCampaign());

            log.debug("Link {}: source={}, campaign={} | clicks={}, views={}, signups={}", 
                    link.getId(), link.getSource(), link.getCampaign(), clicks, views, signups);

            return TrackedLinkAnalyticsDTO.builder()
                    .id(link.getId())
                    .landingSlug(link.getLandingSlug())
                    .source(link.getSource())
                    .medium(link.getMedium())
                    .campaign(link.getCampaign())
                    .generatedLink(link.getGeneratedLink())
                    .adBudget(link.getAdBudget())
                    .timestamp(link.getTimestamp())
                    .clicks(clicks)
                    .views(views)
                    .signups(signups)
                    .build();
        }).collect(Collectors.toList());
    }

    public void deleteLink(Long id) {
        repository.deleteById(id);
    }
}
