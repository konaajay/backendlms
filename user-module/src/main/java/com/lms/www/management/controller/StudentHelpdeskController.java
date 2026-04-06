package com.lms.www.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.dashboard.dto.SupportTicketRequestDTO;
import com.lms.www.management.dashboard.dto.TicketMessageRequestDTO;
import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.enums.TicketStatus;
import com.lms.www.management.model.FAQ;
import com.lms.www.management.model.SupportTicket;
import com.lms.www.management.model.TicketMessage;
import com.lms.www.management.service.StudentHelpdeskService;
import com.lms.www.management.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student/helpdesk")
@RequiredArgsConstructor
public class StudentHelpdeskController {

    private final StudentHelpdeskService helpdeskService;
    private final SecurityUtil securityUtil;

    @PostMapping("/tickets")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicketRequestDTO request) {
        Long studentId = securityUtil.getUserId();
        return new ResponseEntity<>(helpdeskService.createTicket(studentId, request), HttpStatus.CREATED);
    }

    @GetMapping("/tickets")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<Page<SupportTicket>> getMyTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(helpdeskService.getMyTickets(studentId, status, category, PageRequest.of(page, size)));
    }

    @GetMapping("/tickets/{ticketId}")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<Map<String, Object>> getTicketDetails(@PathVariable Long ticketId) {
        Long studentId = securityUtil.getUserId();
        SupportTicket ticket = helpdeskService.getTicketDetails(studentId, ticketId);
        List<TicketMessage> messages = helpdeskService.getTicketMessages(studentId, ticketId);

        return ResponseEntity.ok(Map.of(
                "ticket", ticket,
                "messages", messages));
    }

    @PostMapping("/tickets/{ticketId}/reply")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<TicketMessage> replyToTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketMessageRequestDTO request) {

        Long studentId = securityUtil.getUserId();
        return ResponseEntity.ok(helpdeskService.replyToTicket(studentId, ticketId, request));
    }

    @GetMapping("/faqs")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<List<FAQ>> getFAQs(
            @RequestParam(required = false) TicketCategory category) {
        return ResponseEntity.ok(helpdeskService.getFAQs(category));
    }

    @GetMapping("/support-info")
    // @PreAuthorize("hasAuthority('HELPDESK_VIEW')")
    public ResponseEntity<Map<String, String>> getSupportInfo() {
        return ResponseEntity.ok(helpdeskService.getSupportInfo());
    }
}