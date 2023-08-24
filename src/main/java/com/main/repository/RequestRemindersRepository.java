package com.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.model.RequestReminders;

public interface RequestRemindersRepository extends JpaRepository<RequestReminders, Long> {

	RequestReminders findByRequestReminderId(Long requestReminderId);

	void deleteByRequestReminderId(Long requestReminderId);

}
