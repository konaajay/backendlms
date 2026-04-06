package com.lms.www.fee.ledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.lms.www.fee.ledger.entity.FeeAuditLog;

public interface FeeAuditLogRepository extends JpaRepository<FeeAuditLog, Long> {

    Page<FeeAuditLog> findByModule(String module, Pageable pageable);

    Page<FeeAuditLog> findByEntityNameAndEntityId(String entityName, Long entityId, Pageable pageable);

    @Query("""
        SELECT f FROM FeeAuditLog f
        WHERE (:module IS NULL OR f.module = :module)
        AND (:entityName IS NULL OR f.entityName = :entityName)
        AND (:entityId IS NULL OR f.entityId = :entityId)
        """)
    Page<FeeAuditLog> search(String module, String entityName, Long entityId, Pageable pageable);
}
