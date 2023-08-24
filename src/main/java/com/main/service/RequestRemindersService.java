package com.main.service;

import java.util.ArrayList;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.main.model.EmailSchedule;
import com.main.model.RequestReminders;
import com.main.payload.EmailResponse;
import com.main.payload.RequestRemindersPayload;
import com.main.repository.EmailScheduleRepositroy;
import com.main.repository.RequestRemindersRepository;
import com.main.schedular.EmailSchedular;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class RequestRemindersService {

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RequestRemindersRepository requestRemindersRepository;

	@Autowired
	private EmailSchedular emailSchedular;

	@Autowired
	private EmailScheduleRepositroy emailScheduleRepositroy;

	@Autowired
	private Scheduler scheduler;

	public List<EmailResponse> addRequestRemainders(@Valid List<RequestRemindersPayload> xxaasRequestReminders) {
		List<EmailResponse> emailResponseList = new ArrayList<>();
		try {
			for (RequestRemindersPayload xxaasRequestRemindersdto : xxaasRequestReminders) {
				RequestReminders requestReminders = modelMapper.map(xxaasRequestRemindersdto, RequestReminders.class);
				RequestReminders reminders = requestRemindersRepository.save(requestReminders);
				ResponseEntity<EmailResponse> scheduleEmail = emailSchedular.scheduleEmail(reminders);
				emailResponseList.add(scheduleEmail.getBody());
			}
			emailResponseList.add(new EmailResponse(true, "Email schedule sucessfully"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return emailResponseList;
	}

	public List<EmailResponse> deleteSchedule(List<Long> requestReminderIds) {
		List<EmailResponse> emailResponses = new ArrayList<>();
		try {
			for (Long requestReminderId : requestReminderIds) {
				EmailSchedule emailSchedule = emailScheduleRepositroy.findByRequestReminderId(requestReminderId);
				if (emailSchedule != null) {
					EmailResponse deleteScheduler = emailSchedular.deleteScheduler(emailSchedule);
					if (requestRemindersRepository.existsById(requestReminderId))
						requestRemindersRepository.deleteById(requestReminderId);
					if (emailScheduleRepositroy.existsByRequestReminderId(requestReminderId)) {
						Long scheduledId = emailScheduleRepositroy.findByRequestReminderId(requestReminderId)
								.getScheduleId();
						emailScheduleRepositroy.deleteById(scheduledId);
					}
					emailResponses.add(deleteScheduler);
				} else {
					emailResponses.add(new EmailResponse(false, "Id not not exist"));
				}
			}
			return emailResponses;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception while deleting job", e);
		}
		return List.of(new EmailResponse(false, "Exception while deleting email job"));
	}

	public List<ResponseEntity<EmailResponse>> updateRequestReminder(
			@Valid List<RequestRemindersPayload> xxaasRequestRemindersDto) {
		List<ResponseEntity<EmailResponse>> responseEntities = new ArrayList<>();
		try {
			for (RequestRemindersPayload requestRemindersPayload : xxaasRequestRemindersDto) {
				RequestReminders requestReminder = requestRemindersRepository
						.findByRequestReminderId(requestRemindersPayload.getRequestReminderId());
				requestReminder = modelMapper.map(requestRemindersPayload, RequestReminders.class);
				requestRemindersRepository.save(requestReminder);
				EmailSchedule findByRequestReminderId = emailScheduleRepositroy
						.findByRequestReminderId(requestRemindersPayload.getRequestReminderId());
				TriggerKey triggerKey = new TriggerKey(findByRequestReminderId.getJobName(),
						findByRequestReminderId.getGroupName());
				Trigger oldTrigger = scheduler.getTrigger(triggerKey);
				ResponseEntity<EmailResponse> updateScheduleEmail = emailSchedular.updateScheduleEmail(requestReminder,
						oldTrigger);
				responseEntities.add(updateScheduleEmail);
			}
			return responseEntities;
		} catch (Exception e) {
			log.error("Exception while rescheduling email",e);
		}
		return List.of(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new EmailResponse(false, "Exception while rescheduling email")));
	}
}
