package com.lms.www.campus.model.Transport;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class TransportFeeStructure {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "route_id", nullable = false, unique = true)
  private Long routeId;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal annualFee;

  @Column(name = "academic_year", length = 10)
  private String academicYear;
}