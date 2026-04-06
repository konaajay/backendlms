package com.lms.www.management.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.dashboard.dto.SupportTicketRequestDTO;
import com.lms.www.management.dashboard.dto.TicketMessageRequestDTO;
import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.enums.TicketStatus;
import com.lms.www.management.model.FAQ;
import com.lms.www.management.model.SupportTicket;
import com.lms.www.management.model.TicketMessage;
import com.lms.www.management.repository.FAQRepository;
import com.lms.www.management.repository.SupportTicketRepository;
import com.lms.www.management.repository.TicketMessageRepository;
import com.lms.www.management.service.StudentHelpdeskService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentHelpdeskServiceImpl implements StudentHelpdeskService {

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final FAQRepository faqRepository;

    @Override
    @Transactional
    public SupportTicket createTicket(Long studentId, SupportTicketRequestDTO request) {
        long currentCount = ticketRepository.count();
        String year = String.valueOf(java.time.Year.now().getValue());
        String ticketCode = String.format("TKT-%s-%06d", year, currentCount + 1);

        SupportTicket ticket = SupportTicket.builder()
                .studentId(studentId)
                .ticketCode(ticketCode)
                .subject(request.getSubject())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .build();

        SupportTicket savedTicket = ticketRepository.save(ticket);

        triggerNotification(studentId, savedTicket.getTicketCode(), "Ticket Created");

        return savedTicket;
    }

    @Override
    public Page<SupportTicket> getMyTickets(Long studentId, TicketStatus status, TicketCategory category,
            Pageable pageable) {
        return ticketRepository.findFilteredTicketsByStudent(studentId, status, category, pageable);
    }

    @Override
    public SupportTicket getTicketDetails(Long studentId, Long ticketId) {
        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!ticket.getStudentId().equals(studentId)) {
            throw new RuntimeException("403 Forbidden: You cannot access someone else's ticket.");
        }

        return ticket;
    }

    @Override
    public List<TicketMessage> getTicketMessages(Long studentId, Long ticketId) {
        getTicketDetails(studentId, ticketId);
        return messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    @Override
    @Transactional
    public TicketMessage replyToTicket(Long studentId, Long ticketId, TicketMessageRequestDTO request) {
        SupportTicket ticket = getTicketDetails(studentId, ticketId);

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new RuntimeException("Cannot reply to a closed ticket.");
        }

        TicketMessage message = TicketMessage.builder()
                .ticketId(ticketId)
                .senderType("STUDENT")
                .message(request.getMessage())
                .attachmentUrl(request.getAttachmentUrl())
                .build();

        TicketMessage savedMessage = messageRepository.save(message);

        ticket.setStatus(TicketStatus.OPEN);
        ticketRepository.save(ticket);

        triggerNotification(studentId, ticket.getTicketCode(), "New Student Reply");

        return savedMessage;
    }

    @Override
    public List<FAQ> getFAQs(TicketCategory category) {
        if (category != null) {
            return faqRepository.findByCategory(category);
        }
        return faqRepository.findAll();
    }

    @Override
    public Map<String, String> getSupportInfo() {
        return Map.of(
                "email", "support@lms-edu.com",
                "phone", "+1 800 123 4567");
    }

    private void triggerNotification(Long studentId, String ticketCode, String event) {
        log.info("NOTIFICATION HOOK: {} event for Ticket {} / Student {}", event, ticketCode, studentId);
    }
}