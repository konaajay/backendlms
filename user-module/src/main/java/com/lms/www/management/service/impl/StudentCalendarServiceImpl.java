package com.lms.www.management.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.lms.www.management.dashboard.dto.CalendarEventDTO;
import com.lms.www.management.dashboard.dto.PersonalEventRequestDTO;
import com.lms.www.management.enums.CalendarEventType;
import com.lms.www.management.model.Exam;
import com.lms.www.management.model.Session;
import com.lms.www.management.model.StudentBatch;
import com.lms.www.management.model.StudentCalendarEvent;
import com.lms.www.management.model.WebinarRegistration;
import com.lms.www.management.repository.ExamRepository;
import com.lms.www.management.repository.SessionRepository;
import com.lms.www.management.repository.StudentBatchRepository;
import com.lms.www.management.repository.StudentCalendarEventRepository;
import com.lms.www.management.repository.WebinarRegistrationRepository;
import com.lms.www.management.service.StudentCalendarService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCalendarServiceImpl implements StudentCalendarService {

        private final StudentCalendarEventRepository studentCalendarEventRepository;
        private final StudentBatchRepository studentBatchRepository;
        private final SessionRepository sessionRepository;
        private final ExamRepository examRepository;
        private final WebinarRegistrationRepository webinarRegistrationRepository;

        @Override
        public List<CalendarEventDTO> getStudentCalendarEvents(Long userId, int year, int month) {

                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate firstDayOfMonth = yearMonth.atDay(1);
                LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

                List<CalendarEventDTO> allEvents = new ArrayList<>();

                // 1. Personal Events
                List<StudentCalendarEvent> personalEvents = studentCalendarEventRepository
                                .findByUserIdAndEventDateBetween(userId, firstDayOfMonth, lastDayOfMonth);

                for (StudentCalendarEvent pe : personalEvents) {
                        allEvents.add(CalendarEventDTO.builder()
                                        .id("PER_" + pe.getEventId())
                                        .title(pe.getTitle())
                                        .description(pe.getDescription())
                                        .type(CalendarEventType.PERSONAL)
                                        .date(pe.getEventDate())
                                        .startTime(pe.getStartTime())
                                        .endTime(pe.getEndTime())
                                        .build());
                }

                // Fetch user batches (check both columns for robustness)
                List<StudentBatch> batchesByStudentId = studentBatchRepository.findByStudentId(userId);
                List<StudentBatch> batchesByUserId = studentBatchRepository.findByUserId(userId);
                
                List<StudentBatch> userBatches = new ArrayList<>(batchesByStudentId);
                for (StudentBatch sb : batchesByUserId) {
                    if (userBatches.stream().noneMatch(existing -> existing.getStudentBatchId().equals(sb.getStudentBatchId()))) {
                        userBatches.add(sb);
                    }
                }

                if (!userBatches.isEmpty()) {

                        List<Long> batchIds = userBatches.stream()
                                        .map(StudentBatch::getBatchId)
                                        .filter(java.util.Objects::nonNull)
                                        .distinct()
                                        .collect(Collectors.toList());

                        // 2. Sessions
                        for (Long batchId : batchIds) {
                                List<Session> sessions = sessionRepository.findByBatchId(batchId);

                                for (Session session : sessions) {

                                        if (session.getStartDate() != null &&
                                                        !session.getStartDate().isBefore(firstDayOfMonth) &&
                                                        !session.getStartDate().isAfter(lastDayOfMonth)) {

                                                LocalTime start = session.getStartTime() != null
                                                                ? session.getStartTime()
                                                                : LocalTime.of(0, 0);

                                                int duration = session.getDurationMinutes() != null
                                                                ? session.getDurationMinutes()
                                                                : 60;

                                                LocalTime end = start.plusMinutes(duration);

                                                allEvents.add(CalendarEventDTO.builder()
                                                                .id("SES_" + session.getSessionId())
                                                                .title(session.getSessionName())
                                                                .description((session.getSessionType() != null
                                                                                ? session.getSessionType()
                                                                                : "Session") + " Session") // ✅ SAFE
                                                                .type(CalendarEventType.SESSION)
                                                                .date(session.getStartDate())
                                                                .startTime(start)
                                                                .endTime(end)
                                                                .build());
                                        }
                                }
                        }

                        // 3. Exams
                        List<Exam> exams = examRepository.findByBatchIdInAndIsDeletedFalse(batchIds);

                        for (Exam exam : exams) {

                                if (exam.getStartTime() != null) {

                                        LocalDate examDate = exam.getStartTime().toLocalDate();

                                        if (!examDate.isBefore(firstDayOfMonth) &&
                                                        !examDate.isAfter(lastDayOfMonth)) {

                                                LocalTime start = ((LocalDateTime) exam.getStartTime()).toLocalTime();

                                                int duration = exam.getDurationMinutes() != null
                                                                ? exam.getDurationMinutes()
                                                                : 60;

                                                LocalTime end = start.plusMinutes(duration);

                                                allEvents.add(CalendarEventDTO.builder()
                                                                .id("EXM_" + exam.getExamId())
                                                                .title(exam.getTitle())
                                                                .description("Type: " + exam.getExamType())
                                                                .type(CalendarEventType.EXAM)
                                                                .date(examDate)
                                                                .startTime(start)
                                                                .endTime(end)
                                                                .build());
                                        }
                                }
                        }
                }

                // 4. Webinars
                List<WebinarRegistration> webinarRegs = webinarRegistrationRepository.findByUserId(userId);

                for (WebinarRegistration reg : webinarRegs) {

                        if (reg.getWebinar() != null &&
                                        reg.getWebinar().getStartTime() != null) {

                                LocalDate webDate = reg.getWebinar().getStartTime().toLocalDate();

                                if (!webDate.isBefore(firstDayOfMonth) &&
                                                !webDate.isAfter(lastDayOfMonth)) {

                                        LocalTime start = reg.getWebinar().getStartTime().toLocalTime();

                                        int duration = reg.getWebinar().getDurationMinutes() != null
                                                        ? reg.getWebinar().getDurationMinutes()
                                                        : 60;

                                        LocalTime end = start.plusMinutes(duration);

                                        allEvents.add(CalendarEventDTO.builder()
                                                        .id("WEB_" + reg.getWebinar().getWebinarId())
                                                        .title(reg.getWebinar().getTitle())
                                                        .description(reg.getWebinar().getDescription() != null
                                                                        ? reg.getWebinar().getDescription()
                                                                        : "Webinar Event")
                                                        .type(CalendarEventType.WEBINAR)
                                                        .date(webDate)
                                                        .startTime(start)
                                                        .endTime(end)
                                                        .build());
                                }
                        }
                }

                // Sort
                allEvents.sort(Comparator.comparing(CalendarEventDTO::getDate)
                                .thenComparing(CalendarEventDTO::getStartTime));

                return allEvents;
        }

        @Override
        public void addPersonalEvent(Long userId, PersonalEventRequestDTO request) {

                StudentCalendarEvent event = new StudentCalendarEvent();

                event.setUserId(userId);
                event.setTitle(request.getTitle());
                event.setDescription(request.getDescription());
                event.setEventDate(request.getEventDate());
                event.setStartTime(request.getStartTime());
                event.setEndTime(request.getEndTime());
                event.setEventType(CalendarEventType.PERSONAL);

                studentCalendarEventRepository.save(event);
        }
}