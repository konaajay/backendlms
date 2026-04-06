package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.Question;

public interface SectionQuestionService {

    // Add question to section
    void addQuestionToSection(Long sectionId, Long questionId);

    // Remove question from section
    void removeQuestionFromSection(Long sectionId, Long questionId);

    // Get all questions inside a section
    List<Question> getQuestionsBySection(Long sectionId);
}
