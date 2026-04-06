package com.lms.www.fee.installment.entity;

import com.lms.www.fee.allocation.entity.StudentFee;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "student_installments")
public class StudentInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_fee_id", nullable = false)
    private StudentFee studentFee;

    @Column(name = "installment_number")
    private Integer number;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status")
    private String status = "UNPAID"; // UNPAID, PARTIAL, PAID, OVERDUE

    @Column(name = "is_admission_fee")
    private Boolean isAdmissionFee = false;

    public StudentInstallment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public StudentFee getStudentFee() { return studentFee; }
    public void setStudentFee(StudentFee studentFee) { this.studentFee = studentFee; }
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getIsAdmissionFee() { return isAdmissionFee; }
    public void setIsAdmissionFee(Boolean isAdmissionFee) { this.isAdmissionFee = isAdmissionFee; }
}
