package com.lms.www.fee.controller;

import com.lms.www.fee.dto.AccountCreateRequest;
import com.lms.www.fee.service.FeeAccountService;
import com.lms.www.security.UserContext;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FeeAccountController {

    private final FeeAccountService feeAccountService;
    private final UserContext userContext;

    @PostMapping("/admin/accounts")
    @PreAuthorize("hasAuthority('CREATE_ACCOUNT')")
    public ResponseEntity<Object> createAccount(@RequestBody AccountCreateRequest request) {
        return ResponseEntity.ok(feeAccountService.createAccount(request));
    }

    @GetMapping("/account/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getMyAccount() {
        return ResponseEntity.ok(feeAccountService.getAccountDetails(userContext.getCurrentUserId()));
    }
}