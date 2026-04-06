package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.WebinarChatMessage;

public interface WebinarChatMessageService {

    WebinarChatMessage sendMessage(Long webinarId, Long senderId, String senderName, String message);

    List<WebinarChatMessage> getMessagesByWebinar(Long webinarId);

}
