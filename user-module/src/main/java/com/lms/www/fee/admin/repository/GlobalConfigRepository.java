package com.lms.www.fee.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lms.www.fee.admin.entity.GlobalConfig;
import java.util.Optional;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, String> {
    Optional<GlobalConfig> findByConfigKey(String key);
}
