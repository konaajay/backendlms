package com.lms.www.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.www.management.model.ReturnDamage;
import com.lms.www.management.service.ReturnDamageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/returns")
@RequiredArgsConstructor
public class ReturnDamageController {

    private final ReturnDamageService returnDamageService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('RETURN_DAMAGE_CREATE', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ReturnDamage> processReturn(
            @RequestBody ReturnDamage returnDamage) {

        return ResponseEntity.ok(returnDamageService.processReturn(returnDamage));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('RETURN_DAMAGE_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<List<ReturnDamage>> getAllReturns() {

        return ResponseEntity.ok(returnDamageService.getAllReturns());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RETURN_DAMAGE_VIEW', 'ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ReturnDamage> getReturn(@PathVariable Long id) {

        return ResponseEntity.ok(returnDamageService.getReturnById(id));
    }
}