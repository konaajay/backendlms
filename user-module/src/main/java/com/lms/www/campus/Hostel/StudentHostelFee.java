package com.lms.www.campus.Hostel;
import java.time.LocalDate;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "student_Hostel_fees")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentHostelFee {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "fee_id")
	    private Long feeId;

	    @Column(name = "student_id", nullable = true)
	    private Long studentId;

	    @Column(name = "student_name", nullable = false)
	    private String studentName;

	    @Column(name = "monthly_fee", nullable = false)
	    private Double monthlyFee;

	    @Column(name = "total_fee", nullable = false)
	    private Double totalFee;

	    @Column(name = "amount_paid", nullable = false)
	    private Double amountPaid;

	    @Column(name = "due_amount", nullable = false)
	    private Double dueAmount;

	    @Column(name = "last_payment_date")
	    private LocalDate lastPaymentDate;
	   
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private FeeStatus status;

	    public enum FeeStatus {
	        PAID,
	        PARTIALLY_PAID,
	        DUE
	    }

        // Manual Getters and Setters
        public Long getFeeId() { return feeId; }
        public void setFeeId(Long feeId) { this.feeId = feeId; }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public Double getMonthlyFee() { return monthlyFee; }
        public void setMonthlyFee(Double monthlyFee) { this.monthlyFee = monthlyFee; }

        public Double getTotalFee() { return totalFee; }
        public void setTotalFee(Double totalFee) { this.totalFee = totalFee; }

        public Double getAmountPaid() { return amountPaid; }
        public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

        public Double getDueAmount() { return dueAmount; }
        public void setDueAmount(Double dueAmount) { this.dueAmount = dueAmount; }

        public LocalDate getLastPaymentDate() { return lastPaymentDate; }
        public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

        public FeeStatus getStatus() { return status; }
        public void setStatus(FeeStatus status) { this.status = status; }

        // Manual setter for nested 'student' object from frontend
        @JsonProperty("student")
        public void setStudent(Map<String, Object> student) {
            if (student != null) {
                if (student.get("id") != null) {
                    this.studentId = Long.valueOf(student.get("id").toString());
                } else if (student.get("studentId") != null) {
                    this.studentId = Long.valueOf(student.get("studentId").toString());
                }
                
                if (student.get("studentName") != null) {
                    this.studentName = student.get("studentName").toString();
                } else if (student.get("name") != null) {
                    this.studentName = student.get("name").toString();
                }
            }
        }
	}

