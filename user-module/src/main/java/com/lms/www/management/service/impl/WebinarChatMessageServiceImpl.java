package com.lms.www.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.Webinar;
import com.lms.www.management.model.WebinarChatMessage;
import com.lms.www.management.repository.WebinarChatMessageRepository;
import com.lms.www.management.repository.WebinarRepository;
import com.lms.www.management.service.WebinarChatMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class WebinarChatMessageServiceImpl implements WebinarChatMessageService {

    private final WebinarChatMessageRepository chatMessageRepository;
    private final WebinarRepository webinarRepository;

    @Override
    public WebinarChatMessage sendMessage(Long webinarId, Long senderId, String senderName, String message) {

        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content must not be empty");
        }

        if (senderName == null || senderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sender name must not be null");
        }

        Webinar webinar = webinarRepository.findById(webinarId)
                .orElseThrow(() -> new IllegalArgumentException("Webinar does not exist"));

        WebinarChatMessage chatMessage = WebinarChatMessage.builder()
                .webinar(webinar)
                .senderId(senderId)
                .senderName(senderName)
                .message(message)
                .build();

        return chatMessageRepository.save(chatMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WebinarChatMessage> getMessagesByWebinar(Long webinarId) {

        if (!webinarRepository.existsById(webinarId)) {
            throw new IllegalArgumentException("Webinar does not exist");
        }

        return chatMessageRepository.findByWebinar_WebinarIdOrderBySentAtAsc(webinarId);
    }
}
