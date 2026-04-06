package com.lms.www.campus.repository.Transport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.TransportPayments;

@Repository
public interface TransportPaymentRepository
        extends JpaRepository<TransportPayments, String> {

    List<TransportPayments> findByStudentId(Long studentId);
}