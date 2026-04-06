package com.lms.www.fee.allocation.entity;

import com.lms.www.fee.structure.entity.FeeStructure;
import com.lms.www.fee.installment.entity.StudentInstallment;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student_fees")
public class StudentFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(name = "discount_type")
    private String discountType; // FLAT, PERCENT

    @Column(name = "discount_value", precision = 12, scale = 2)
    private BigDecimal discountValue = BigDecimal.ZERO;

    @Column(name = "discount_reason")
    private String discountReason;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "total_payable", precision = 12, scale = 2)
    private BigDecimal totalPayable;

    @Column(name = "total_paid", precision = 12, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, PARTIAL, PAID, REFUNDED

    @OneToMany(mappedBy = "studentFee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentInstallment> installments = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime assignedAt;

    public StudentFee() {}

    public void addInstallment(StudentInstallment inst) {
        if (installments == null) installments = new ArrayList<>();
        installments.add(inst);
        inst.setStudentFee(this);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long id) { this.studentId = id; }
    public FeeStructure getFeeStructure() { return feeStructure; }
    public void setFeeStructure(FeeStructure structure) { this.feeStructure = structure; }
    public String getDiscountType() { return discountType; }
    public void setDiscountType(String type) { this.discountType = type; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal val) { this.discountValue = val; }
    public String getDiscountReason() { return discountReason; }
    public void setDiscountReason(String reason) { this.discountReason = reason; }
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String by) { this.approvedBy = by; }
    public BigDecimal getTotalPayable() { return totalPayable; }
    public void setTotalPayable(BigDecimal amt) { this.totalPayable = amt; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal amt) { this.totalPaid = amt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<StudentInstallment> getInstallments() { return installments; }
    public void setInstallments(List<StudentInstallment> list) { this.installments = list; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime dt) { this.assignedAt = dt; }
}
