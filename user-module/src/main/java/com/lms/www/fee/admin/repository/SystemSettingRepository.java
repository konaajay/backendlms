package com.lms.www.fee.admin.repository;

import com.lms.www.fee.admin.entity.SystemSetting;
import com.lms.www.fee.enums.MasterSettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, String> {
    List<SystemSetting> findByTypeAndActiveTrue(MasterSettingType type);
    Optional<SystemSetting> findByTypeAndKey(MasterSettingType type, String key);
}
