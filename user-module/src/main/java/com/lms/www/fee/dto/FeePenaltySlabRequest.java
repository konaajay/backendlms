package com.lms.www.fee.dto;

import com.lms.www.fee.penalty.entity.PenaltyType;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeePenaltySlabRequest {
    private Object id;
    private Long configId;
    private Long feeStructureId;
    private Integer fromDay;
    private Integer toDay;
    private PenaltyType penaltyType;
    private BigDecimal value;
    private boolean active;

    public FeePenaltySlabRequest() {}

    public FeePenaltySlabRequest(Object id, Long configId, Long feeStructureId, Integer fromDay, Integer toDay, PenaltyType penaltyType, BigDecimal value, boolean active) {
        this.id = id;
        this.configId = configId;
        this.feeStructureId = feeStructureId;
        this.fromDay = fromDay;
        this.toDay = toDay;
        this.penaltyType = penaltyType;
        this.value = value;
        this.active = active;
    }

    public Long getId() {
        if (id instanceof Number) return ((Number) id).longValue();
        if (id instanceof String) {
            try { return Long.parseLong((String) id); } catch (Exception e) { return null; }
        }
        return null;
    }
    public void setId(Object id) { this.id = id; }

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }

    public Long getFeeStructureId() { return feeStructureId; }
    public void setFeeStructureId(Long feeStructureId) { this.feeStructureId = feeStructureId; }

    public Integer getFromDay() { return fromDay; }
    public void setFromDay(Integer fromDay) { this.fromDay = fromDay; }

    public Integer getToDay() { return toDay; }
    public void setToDay(Integer toDay) { this.toDay = toDay; }

    public PenaltyType getPenaltyType() { return penaltyType; }
    public void setPenaltyType(PenaltyType penaltyType) { this.penaltyType = penaltyType; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
