package com.lms.www.management.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.www.management.model.QuestionOption;
import com.lms.www.management.service.QuestionOptionService;
import com.lms.www.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/questions/{questionId}/options")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    public QuestionOptionController(
            QuestionOptionService questionOptionService) {
        this.questionOptionService = questionOptionService;
    }

    // 1️⃣ POST TEXT OPTIONS (JSON)
    @PostMapping(consumes = "application/json")
    @PreAuthorize("hasAnyAuthority('QUESTION_OPTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<QuestionOption>> addOptionsJson(
            @PathVariable Long questionId,
            @RequestBody List<QuestionOption> options) {

        return ResponseEntity.ok(
                questionOptionService.addOptions(questionId, options));
    }

    // 2️⃣ POST IMAGE OPTIONS (MULTIPART)
    @PostMapping(value = "/images", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyAuthority('QUESTION_OPTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<QuestionOption>> addOptionsWithImages(
            @PathVariable Long questionId,
            @RequestParam(required = false) List<String> optionText,
            @RequestParam(required = false) List<Boolean> isCorrect,
            @RequestParam(required = false) List<MultipartFile> optionImage,
            @RequestParam(required = false) List<Integer> imageIndex) {

        List<QuestionOption> options = new ArrayList<>();
        int total = 0;

        if (optionText != null)
            total = optionText.size();

        for (int i = 0; i < total; i++) {
            QuestionOption option = new QuestionOption();
            option.setQuestionId(questionId);

            if (i < optionText.size()) {
                option.setOptionText(optionText.get(i));
            }

            if (isCorrect != null && i < isCorrect.size()) {
                option.setIsCorrect(isCorrect.get(i));
            } else {
                option.setIsCorrect(false);
            }

            if (imageIndex != null && optionImage != null) {
                int fileListIndex = imageIndex.indexOf(i);
                if (fileListIndex != -1 && fileListIndex < optionImage.size()) {
                    MultipartFile file = optionImage.get(fileListIndex);
                    if (file != null && !file.isEmpty()) {
                        String imageUrl = FileUploadUtil.saveFile(file, "options");
                        option.setOptionImageUrl(imageUrl);
                    }
                }
            }

            options.add(option);
        }

        return ResponseEntity.ok(
                questionOptionService.addOptions(questionId, options));
    }

    // 3️⃣ GET OPTIONS
    @GetMapping
    // @PreAuthorize("hasAnyAuthority('QUESTION_OPTION_VIEW', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<List<QuestionOption>> getOptions(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionOptionService.getOptionsByQuestion(questionId));
    }

    // 4️⃣ DELETE OPTION
    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasAnyAuthority('QUESTION_OPTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long optionId) {

        questionOptionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }

    // 5️⃣ UPDATE OPTION
    @PutMapping(value = "/{optionId}", consumes = "application/json")
    @PreAuthorize("hasAnyAuthority('QUESTION_OPTION_MANAGE', 'ROLE_ADMIN', 'ALL_PERMISSIONS', 'ROLE_INSTRUCTOR')")
    public ResponseEntity<QuestionOption> updateOption(
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestBody QuestionOption request) {

        return ResponseEntity.ok(
                questionOptionService.updateOption(questionId, optionId, request));
    }
}