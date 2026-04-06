package com.lms.www.campus.repository.Hostel;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.www.campus.Hostel.MessDayMenu;

@Repository
public interface MessDayMenuRepository extends JpaRepository<MessDayMenu, Long> {

    Optional<MessDayMenu> findByDay(MessDayMenu.DayOfWeek day);
}