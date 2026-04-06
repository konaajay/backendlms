package com.lms.www.management.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lms.www.management.dashboard.dto.SupportTicketRequestDTO;
import com.lms.www.management.dashboard.dto.TicketMessageRequestDTO;
import com.lms.www.management.enums.TicketCategory;
import com.lms.www.management.enums.TicketStatus;
import com.lms.www.management.model.FAQ;
import com.lms.www.management.model.SupportTicket;
import com.lms.www.management.model.TicketMessage;

public interface StudentHelpdeskService {

    SupportTicket createTicket(Long studentId, SupportTicketRequestDTO request);

    Page<SupportTicket> getMyTickets(Long studentId, TicketStatus status, TicketCategory category, Pageable pageable);

    SupportTicket getTicketDetails(Long studentId, Long ticketId);

    List<TicketMessage> getTicketMessages(Long studentId, Long ticketId);

    TicketMessage replyToTicket(Long studentId, Long ticketId, TicketMessageRequestDTO request);

    List<FAQ> getFAQs(TicketCategory category);

    Map<String, String> getSupportInfo();
}