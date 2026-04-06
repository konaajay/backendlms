package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.SessionContent;

public interface SessionContentService {

    // ================= CREATE =================
    SessionContent createSessionContent(
            Long sessionId,
            SessionContent sessionContent
    );

    // ================= GET BY ID =================
    SessionContent getSessionContentById(
            Long sessionContentId
    );

    // ================= GET BY SESSION =================
    List<SessionContent> getContentsBySessionId(
            Long sessionId
    );

    // ================= UPDATE =================
    SessionContent updateSessionContent(
            Long sessionContentId,
            SessionContent sessionContent
    );

    // ================= DELETE =================
    void deleteSessionContent(
            Long sessionContentId
    );
}