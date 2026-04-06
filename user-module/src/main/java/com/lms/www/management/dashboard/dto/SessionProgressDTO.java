package com.lms.www.management.dashboard.dto;

import java.io.Serializable;
import java.util.List;

public class SessionProgressDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<SessionContentDTO> contents;
    private Long sessionId;
    private String sessionName;
    private String type;
    private boolean completed;
    private double videoProgressPercentage;

    public SessionProgressDTO() {}
    public SessionProgressDTO(List<SessionContentDTO> contents, Long sessionId, String sessionName, String type, boolean completed, double videoProgressPercentage) {
        this.contents = contents;
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.type = type;
        this.completed = completed;
        this.videoProgressPercentage = videoProgressPercentage;
    }

    public List<SessionContentDTO> getContents() { return contents; }
    public void setContents(List<SessionContentDTO> contents) { this.contents = contents; }
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }
    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public double getVideoProgressPercentage() { return videoProgressPercentage; }
    public void setVideoProgressPercentage(double videoProgressPercentage) { this.videoProgressPercentage = videoProgressPercentage; }

    public static SessionProgressDTOBuilder builder() { return new SessionProgressDTOBuilder(); }
    public static class SessionProgressDTOBuilder {
        private List<SessionContentDTO> contents;
        private Long sessionId;
        private String sessionName;
        private String type;
        private boolean completed;
        private double videoProgressPercentage;

        public SessionProgressDTOBuilder contents(List<SessionContentDTO> contents) { this.contents = contents; return this; }
        public SessionProgressDTOBuilder sessionId(Long id) { this.sessionId = id; return this; }
        public SessionProgressDTOBuilder sessionName(String name) { this.sessionName = name; return this; }
        public SessionProgressDTOBuilder type(String type) { this.type = type; return this; }
        public SessionProgressDTOBuilder completed(boolean completed) { this.completed = completed; return this; }
        public SessionProgressDTOBuilder videoProgressPercentage(double percentage) { this.videoProgressPercentage = percentage; return this; }
        public SessionProgressDTO build() {
            return new SessionProgressDTO(contents, sessionId, sessionName, type, completed, videoProgressPercentage);
        }
    }
}