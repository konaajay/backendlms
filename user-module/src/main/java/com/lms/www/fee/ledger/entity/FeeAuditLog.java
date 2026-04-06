package com.lms.www.fee.ledger.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "fee_audit_logs",
    indexes = {
        @Index(name = "idx_module", columnList = "module"),
        @Index(name = "idx_entity", columnList = "entity_name, entity_id"),
        @Index(name = "idx_performed_at", columnList = "performed_at")
    }
)
public class FeeAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Action action;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "performed_at", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    public FeeAuditLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }

    public enum Action {
        CREATE, UPDATE, DELETE, VIEW
    }

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }

    public static FeeAuditLogBuilder builder() {
        return new FeeAuditLogBuilder();
    }

    public static class FeeAuditLogBuilder {
        private FeeAuditLog instance = new FeeAuditLog();

        public FeeAuditLogBuilder id(Long id) { instance.setId(id); return this; }
        public FeeAuditLogBuilder module(String module) { instance.setModule(module); return this; }
        public FeeAuditLogBuilder entityName(String name) { instance.setEntityName(name); return this; }
        public FeeAuditLogBuilder entityId(Long id) { instance.setEntityId(id); return this; }
        public FeeAuditLogBuilder action(Action action) { instance.setAction(action); return this; }
        public FeeAuditLogBuilder oldValue(String value) { instance.setOldValue(value); return this; }
        public FeeAuditLogBuilder newValue(String value) { instance.setNewValue(value); return this; }
        public FeeAuditLogBuilder performedBy(Long by) { instance.setPerformedBy(by); return this; }
        public FeeAuditLogBuilder ipAddress(String ip) { instance.setIpAddress(ip); return this; }
        public FeeAuditLogBuilder performedAt(LocalDateTime at) { instance.setPerformedAt(at); return this; }
        public FeeAuditLog build() { return instance; }
    }
}
