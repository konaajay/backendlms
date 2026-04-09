package com.lms.www.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coding_test_case")
@Getter
@Setter
public class CodingTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_case_id")
    private Long testCaseId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "input_data", columnDefinition = "TEXT", nullable = false)
    private String inputData;

    @Column(name = "expected_output", columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    @Column(name = "hidden", nullable = false)
    private Boolean hidden;

    // Manual Getters
    public String getInputData() { return inputData; }
    public String getExpectedOutput() { return expectedOutput; }
    public Boolean getHidden() { return hidden; }
}
