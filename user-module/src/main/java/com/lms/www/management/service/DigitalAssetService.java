package com.lms.www.management.service;

import java.util.List;

import com.lms.www.management.model.DigitalAsset;

public interface DigitalAssetService {

    DigitalAsset createAsset(DigitalAsset asset);

    List<DigitalAsset> getAllAssets();

    DigitalAsset getAssetById(Long id);

    DigitalAsset assignLicense(Long id, String userId);

    DigitalAsset releaseLicense(Long id);

    void deleteAsset(Long id);
}