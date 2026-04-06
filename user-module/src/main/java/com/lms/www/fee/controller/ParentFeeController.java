package com.lms.www.fee.controller;

import com.lms.www.fee.dto.StudentLedgerResponse;
import com.lms.www.fee.service.StudentFeeAllocationService;
import com.lms.www.repository.ParentRepository;
import com.lms.www.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.lms.www.fee.dto.ApiResponse;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentFeeController {

    private final StudentFeeAllocationService allocationService;
    private final ParentRepository parentRepository;
    private final UserContext userContext;

    @GetMapping("/child-ledger/{childUserId}")
    @PreAuthorize("hasAuthority('PARENT_CHILD_VIEW') or hasRole('PARENT')")
    public ResponseEntity<ApiResponse<StudentLedgerResponse>> getChildLedger(@PathVariable Long childUserId) {
        Long parentUserId = userContext.getCurrentUserId();
        
        // Verify that this student belongs to the requesting parent
        boolean isParent = parentRepository.findByUser_UserId(parentUserId).stream()
                .anyMatch(p -> p.getStudents().stream()
                        .anyMatch(rel -> rel.getStudent() != null && 
                                        rel.getStudent().getUser() != null && 
                                        rel.getStudent().getUser().getUserId().equals(childUserId)));

        if (!isParent) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This student is not registered as your child.");
        }

        return ResponseEntity.ok(ApiResponse.success(allocationService.getStudentLedger(childUserId)));
    }
}