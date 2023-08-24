package com.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.model.EmailSchedule;

public interface EmailScheduleRepositroy extends JpaRepository<EmailSchedule, Long>{

	EmailSchedule findByRequestReminderId(Long requestReminderId);

	boolean existsByRequestReminderId(Long requestReminderId);

}
