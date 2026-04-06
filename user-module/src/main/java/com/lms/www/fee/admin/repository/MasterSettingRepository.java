package com.lms.www.fee.admin.repository;

import com.lms.www.fee.admin.entity.MasterSetting;
import com.lms.www.fee.enums.MasterSettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MasterSettingRepository extends JpaRepository<MasterSetting, Long> {
    Optional<MasterSetting> findByTypeAndKeyNameAndActiveTrue(MasterSettingType type, String keyName);
    List<MasterSetting> findByTypeAndActiveTrue(MasterSettingType type);
    Optional<MasterSetting> findByTypeAndKeyName(MasterSettingType type, String keyName);
    Optional<MasterSetting> findByKeyName(String keyName);
    List<MasterSetting> findByActiveTrue();
}
