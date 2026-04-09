package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.lms.www.management.model.TopicContent;

public interface TopicContentRepository extends JpaRepository<TopicContent, Long> {

    List<TopicContent> findByTopicTopicId(Long topicId);

    @Query("select tc.topic.course.allowOfflineMobile from TopicContent tc where tc.contentId = :contentId")
    Boolean findAllowOfflineFlag(@Param("contentId") Long contentId);

    @Modifying
    @Transactional
    @Query("delete from TopicContent tc where tc.topic.course.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Long courseId);

    long countByTopicTopicId(Long topicId);
}