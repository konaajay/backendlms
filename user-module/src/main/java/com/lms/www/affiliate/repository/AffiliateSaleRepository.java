package com.lms.www.affiliate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.affiliate.entity.Affiliate;
import com.lms.www.affiliate.entity.AffiliateSale;

public interface AffiliateSaleRepository extends JpaRepository<AffiliateSale, Long> {
    List<AffiliateSale> findByAffiliate(Affiliate affiliate);
    List<AffiliateSale> findByAffiliateId(Long affiliateId);
    List<AffiliateSale> findByAffiliateAndStatus(Affiliate affiliate, AffiliateSale.SaleStatus status);
    List<AffiliateSale> findByAffiliateIdAndStatus(Long affiliateId, AffiliateSale.SaleStatus status);

    Optional<AffiliateSale> findByOrderId(String orderId);
    List<AffiliateSale> findByStatus(AffiliateSale.SaleStatus status);
    
    long countByAffiliate(Affiliate affiliate);
    long countByAffiliateId(Long affiliateId);
    
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    Optional<AffiliateSale> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<AffiliateSale> findByStudentId(Long studentId);
    
    // Aggregate queries for metrics
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.orderAmount), 0) FROM AffiliateSale s WHERE s.affiliate.id = :affiliateId AND s.status IN :statuses")
    java.math.BigDecimal sumRevenueByAffiliateAndStatuses(@org.springframework.data.repository.query.Param("affiliateId") Long affiliateId, @org.springframework.data.repository.query.Param("statuses") java.util.List<AffiliateSale.SaleStatus> statuses);
    
    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.orderAmount), 0) FROM AffiliateSale s WHERE s.affiliate.id = :affiliateId AND s.status IN :statuses")
    java.math.BigDecimal sumRevenueByAffiliateIdAndStatuses(@org.springframework.data.repository.query.Param("affiliateId") Long affiliateId, @org.springframework.data.repository.query.Param("statuses") java.util.List<AffiliateSale.SaleStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.commissionAmount), 0) FROM AffiliateSale s WHERE s.affiliate.id = :affiliateId AND s.batchId = :batchId AND s.status IN :statuses")
    java.math.BigDecimal sumCommissionByAffiliateIdAndBatchIdAndStatuses(@org.springframework.data.repository.query.Param("affiliateId") Long affiliateId, @org.springframework.data.repository.query.Param("batchId") Long batchId, @org.springframework.data.repository.query.Param("statuses") java.util.List<AffiliateSale.SaleStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(s.commissionAmount), 0) FROM AffiliateSale s WHERE s.affiliate.id = :affiliateId AND s.status IN :statuses")
    java.math.BigDecimal sumCommissionByAffiliateIdAndStatuses(@org.springframework.data.repository.query.Param("affiliateId") Long affiliateId, @org.springframework.data.repository.query.Param("statuses") java.util.List<AffiliateSale.SaleStatus> statuses);

    long countByAffiliateIdAndStatusIn(Long affiliateId, java.util.List<AffiliateSale.SaleStatus> statuses);
}
