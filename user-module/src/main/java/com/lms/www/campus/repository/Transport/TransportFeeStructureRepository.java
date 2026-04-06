package com.lms.www.campus.repository.Transport;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.TransportFeeStructure;

@Repository
public interface TransportFeeStructureRepository
        extends JpaRepository<TransportFeeStructure, Long> {

    Optional<TransportFeeStructure> findByRouteId(Long routeId);
}
