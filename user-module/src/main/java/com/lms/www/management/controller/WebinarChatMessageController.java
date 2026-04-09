package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.WebinarChatMessage;
import com.lms.www.management.service.WebinarChatMessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/webinar-chat")
@RequiredArgsConstructor
public class WebinarChatMessageController {

    private final WebinarChatMessageService chatMessageService;

    // =========================================================
    // 💬 SEND CHAT MESSAGE (Old)
    // =========================================================
    @PostMapping("/send")
    @PreAuthorize("hasAuthority('WEBINAR_CHAT_SEND')")
    public ResponseEntity<WebinarChatMessage> sendChatMessage(@RequestBody Map<String, Object> payload) {
        return processMessage(payload);
    }

    // =========================================================
    // 💬 SEND CHAT MESSAGE (New - matches Doc/FE)
    // =========================================================
    @PostMapping("/webinar/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_CHAT_SEND')")
    public ResponseEntity<WebinarChatMessage> sendChatMessageByWebinar(
            @PathVariable Long webinarId,
            @RequestBody Map<String, Object> payload) {
        payload.put("webinarId", webinarId);
        return processMessage(payload);
    }

    private ResponseEntity<WebinarChatMessage> processMessage(Map<String, Object> payload) {
        Long webinarId = Long.valueOf(payload.get("webinarId").toString());
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        String senderName = (String) payload.get("senderName");
        String message = (String) payload.get("message");

        WebinarChatMessage chatMessage = chatMessageService.sendMessage(webinarId, senderId, senderName, message);

        return ResponseEntity.status(201).body(chatMessage);
    }

    // =========================================================
    // 💬 GET ALL CHAT MESSAGES BY WEBINAR
    // =========================================================
    @GetMapping("/webinar/{webinarId}")
    @PreAuthorize("hasAuthority('WEBINAR_CHAT_VIEW')")
    public ResponseEntity<List<WebinarChatMessage>> getMessagesByWebinar(@PathVariable Long webinarId) {

        List<WebinarChatMessage> messages = chatMessageService.getMessagesByWebinar(webinarId);
        return ResponseEntity.ok(messages);
    }

}
