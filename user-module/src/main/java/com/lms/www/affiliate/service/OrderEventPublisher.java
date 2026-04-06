package com.lms.www.affiliate.service;

import com.lms.www.affiliate.event.OrderCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishOrder(OrderCompletedEvent event) {
        // Simulating the action that happens in Order Service
        applicationEventPublisher.publishEvent(event);
    }
}
