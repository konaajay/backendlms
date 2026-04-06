package com.lms.www.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.www.management.model.DigitalAsset;
import com.lms.www.management.repository.DigitalAssetRepository;
import com.lms.www.management.service.DigitalAssetService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DigitalAssetServiceImpl implements DigitalAssetService {

    private final DigitalAssetRepository assetRepository;

    @Override
    public DigitalAsset createAsset(DigitalAsset asset) {

        // ✅ validation
        if (asset.getTotalLicenses() == null || asset.getTotalLicenses() <= 0) {
            throw new RuntimeException("Total licenses must be greater than 0");
        }

        // ✅ defaults
        asset.setCreatedAt(LocalDateTime.now());
        asset.setUsedLicenses(0);

        if (asset.getStatus() == null) {
            asset.setStatus("ACTIVE");
        }

        if (asset.getRemarks() == null) {
            asset.setRemarks("N/A");
        }

        asset.setUpdatedAt(LocalDateTime.now());

        return assetRepository.save(asset);
    }

    @Override
    public List<DigitalAsset> getAllAssets() {
        return assetRepository.findAll();
    }

    @Override
    public DigitalAsset getAssetById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Digital asset not found"));
    }

    @Override
    public DigitalAsset assignLicense(Long id, Long userId) {

        DigitalAsset asset = getAssetById(id);

        int used = asset.getUsedLicenses() != null ? asset.getUsedLicenses() : 0;
        int total = asset.getTotalLicenses() != null ? asset.getTotalLicenses() : 0;

        if (used >= total) {
            throw new RuntimeException("No licenses available");
        }

        used += 1;
        asset.setUsedLicenses(used);
        asset.setUpdatedAt(LocalDateTime.now());

        return assetRepository.save(asset);
    }

    @Override
    public DigitalAsset releaseLicense(Long id) {

        DigitalAsset asset = getAssetById(id);

        int used = asset.getUsedLicenses() != null ? asset.getUsedLicenses() : 0;

        if (used <= 0) {
            throw new RuntimeException("No licenses to release");
        }

        used -= 1;
        asset.setUsedLicenses(used);
        asset.setUpdatedAt(LocalDateTime.now());

        return assetRepository.save(asset);
    }

    @Override
    public void deleteAsset(Long id) {
        assetRepository.deleteById(id);
    }
}