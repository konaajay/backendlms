package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.TopicContent;

public interface TopicContentService {

    TopicContent createContent(Long topicId, TopicContent content);

    List<TopicContent> createContentBulk(Long topicId, List<TopicContent> contents);

    List<TopicContent> getAllContents();

    TopicContent getContentById(Long contentId);

    List<TopicContent> getContentsByTopicId(Long topicId);

    TopicContent updateContent(Long contentId, TopicContent incoming);

    void deleteContent(Long contentId);
}