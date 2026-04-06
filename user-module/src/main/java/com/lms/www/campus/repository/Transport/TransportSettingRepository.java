package com.lms.www.campus.repository.Transport;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Transport.TransportSetting;

@Repository
public interface TransportSettingRepository
        extends JpaRepository<TransportSetting, Long> {

    Optional<TransportSetting> findByKeyName(String keyName);
}
