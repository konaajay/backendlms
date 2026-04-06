package com.lms.www.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.Approval;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    List<Approval> findByStatus(String status);
}