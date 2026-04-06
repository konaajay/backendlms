package com.lms.www.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.management.model.DigitalAsset;

public interface DigitalAssetRepository extends JpaRepository<DigitalAsset, Long> {
}