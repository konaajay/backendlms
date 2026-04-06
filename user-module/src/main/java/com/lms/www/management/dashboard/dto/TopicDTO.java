package com.lms.www.management.dashboard.dto;

import java.util.List;

public class TopicDTO {
    private Long topicId;
    private String topicName;
    private Integer topicOrder;
    private List<SessionProgressDTO> sessions;
    private List<TopicContentDTO> contents;

    public TopicDTO() {}
    public TopicDTO(Long topicId, String topicName, Integer topicOrder, List<SessionProgressDTO> sessions, List<TopicContentDTO> contents) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.topicOrder = topicOrder;
        this.sessions = sessions;
        this.contents = contents;
    }

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    public Integer getTopicOrder() { return topicOrder; }
    public void setTopicOrder(Integer topicOrder) { this.topicOrder = topicOrder; }
    public List<SessionProgressDTO> getSessions() { return sessions; }
    public void setSessions(List<SessionProgressDTO> sessions) { this.sessions = sessions; }
    public List<TopicContentDTO> getContents() { return contents; }
    public void setContents(List<TopicContentDTO> contents) { this.contents = contents; }

    public static TopicDTOBuilder builder() { return new TopicDTOBuilder(); }
    public static class TopicDTOBuilder {
        private Long topicId;
        private String topicName;
        private Integer topicOrder;
        private List<SessionProgressDTO> sessions;
        private List<TopicContentDTO> contents;

        public TopicDTOBuilder topicId(Long id) { this.topicId = id; return this; }
        public TopicDTOBuilder topicName(String name) { this.topicName = name; return this; }
        public TopicDTOBuilder topicOrder(Integer order) { this.topicOrder = order; return this; }
        public TopicDTOBuilder sessions(List<SessionProgressDTO> sessions) { this.sessions = sessions; return this; }
        public TopicDTOBuilder contents(List<TopicContentDTO> contents) { this.contents = contents; return this; }
        public TopicDTO build() {
            return new TopicDTO(topicId, topicName, topicOrder, sessions, contents);
        }
    }
}