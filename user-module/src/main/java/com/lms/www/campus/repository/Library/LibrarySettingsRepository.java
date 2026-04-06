package com.lms.www.campus.repository.Library;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.www.campus.Library.LibrarySettings;

public interface LibrarySettingsRepository extends JpaRepository<LibrarySettings, Long> {

	List<LibrarySettings> findByIsDeletedFalse();

	Optional<LibrarySettings> findFirstByIsDeletedFalse();

	Optional<LibrarySettings> findByMemberRoleAndIsDeletedFalse(String memberRole);

}