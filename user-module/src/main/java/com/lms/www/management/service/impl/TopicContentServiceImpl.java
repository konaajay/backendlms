package com.lms.www.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.common.exception.ResourceNotFoundException;
import com.lms.www.management.model.Topic;
import com.lms.www.management.model.TopicContent;
import com.lms.www.management.repository.TopicContentRepository;
import com.lms.www.management.repository.TopicRepository;
import com.lms.www.management.service.TopicContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicContentServiceImpl implements TopicContentService {

    private final TopicContentRepository topicContentRepository;
    private final TopicRepository topicRepository;

    // ===============================
    // CREATE SINGLE CONTENT
    // ===============================
    @Override
    public TopicContent createContent(Long topicId, TopicContent content) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found with id: " + topicId
                        )
                );

        content.setTopic(topic);

        if (content.getFileUrl() != null && content.getFileUrl().startsWith("http")) {
            content.setContentSource("URL");
        } else {
            content.setContentSource("UPLOAD");
        }

        return topicContentRepository.save(content);
    }

    // ===============================
    // CREATE BULK
    // ===============================
    @Override
    public List<TopicContent> createContentBulk(
            Long topicId,
            List<TopicContent> contents
    ) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found with id: " + topicId
                        )
                );

        List<TopicContent> saved = new ArrayList<>();

        for (TopicContent content : contents) {

            content.setTopic(topic);

            if (content.getFileUrl() != null && content.getFileUrl().startsWith("http")) {
                content.setContentSource("URL");
            } else {
                content.setContentSource("UPLOAD");
            }

            saved.add(topicContentRepository.save(content));
        }

        return saved;
    }

    // ===============================
    // GET CONTENT BY ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public TopicContent getContentById(Long contentId) {

        return topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );
    }

    // ===============================
    // GET CONTENTS BY TOPIC
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getContentsByTopicId(Long topicId) {

        return topicContentRepository.findByTopicTopicId(topicId);
    }

    // ===============================
    // GET ALL CONTENTS
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getAllContents() {
        return topicContentRepository.findAll();
    }

    // ===============================
    // UPDATE
    // ===============================
    @Override
    public TopicContent updateContent(Long contentId, TopicContent incoming) {

        TopicContent existing = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );

        if (incoming.getContentType() != null)
            existing.setContentType(incoming.getContentType());

        if (incoming.getContentTitle() != null)
            existing.setContentTitle(incoming.getContentTitle());

        if (incoming.getContentDescription() != null)
            existing.setContentDescription(incoming.getContentDescription());

        if (incoming.getContentOrder() != null)
            existing.setContentOrder(incoming.getContentOrder());

        if (incoming.getFileUrl() != null)
            existing.setFileUrl(incoming.getFileUrl());

        return topicContentRepository.save(existing);
    }

    // ===============================
    // DELETE
    // ===============================
    @Override
    public void deleteContent(Long contentId) {

        TopicContent existing = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );

        topicContentRepository.delete(existing);
    }
}