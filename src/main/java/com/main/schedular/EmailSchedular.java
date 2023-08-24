package com.main.schedular;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.main.model.EmailSchedule;
import com.main.model.RequestReminders;
import com.main.payload.EmailResponse;
import com.main.repository.EmailScheduleRepositroy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class EmailSchedular {

	@Autowired
	private Scheduler scheduler;

	@Autowired
	private EmailScheduleRepositroy emailScheduleRepositroy;
	
	@Autowired
	private ModelMapper modelMapper;

	public ResponseEntity<EmailResponse> scheduleEmail(RequestReminders requestReminder) {
		try {
			LocalDate date = requestReminder.getStartDate().toInstant()
					.atZone(ZoneId.of(requestReminder.getZoneId())).toLocalDate();
			LocalTime time = LocalTime.now().plusMinutes(1);

			ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time,
					ZoneId.of(requestReminder.getZoneId()));
			String emailJob = requestReminder.getRequestReminderId() + "-" + requestReminder.getRequestId()
					+ "-emailJob";
			String emailGroup = requestReminder.getRequestReminderId() + "-" + requestReminder.getRequestId()
					+ "-emailGroup";

			if (zonedDateTime.isBefore(ZonedDateTime.now())) {
				EmailResponse emailResponse = new EmailResponse(false, "DateTime must be after the current time");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
			}
			JobDetail jobDetail = buildJobDetail(requestReminder,emailJob,emailGroup);
			Trigger trigger = buildTrigger(jobDetail, zonedDateTime, requestReminder.getDaysToRemind(),
					requestReminder.getEndDate(), emailGroup);
			scheduler.scheduleJob(jobDetail, trigger);
			EmailSchedule emailSchedule = new EmailSchedule(requestReminder.getRequestId(),
					requestReminder.getRequestReminderId(), Date.from(requestReminder.getStartDate().toInstant()),
					Date.from(requestReminder.getEndDate().toInstant()), emailJob, emailGroup, trigger.toString(),
					requestReminder.getDaysToRemind(), trigger.getJobKey().toString());

			emailScheduleRepositroy.save(emailSchedule);
			EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(),
					jobDetail.getKey().getGroup(), "Email scheduled sucessfully");
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(emailResponse);
		} catch (Exception e) {
			log.error("Exception while scheduling email", e);
			EmailResponse emailResponse = new EmailResponse(false,
					"Error while scheduling email, Please tryt again later");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
		}
	}

	public ResponseEntity<EmailResponse> updateScheduleEmail(RequestReminders requestReminder, Trigger oldTrigger) {
		
		try {
			LocalDate date = requestReminder.getStartDate().toInstant()
					.atZone(ZoneId.of(requestReminder.getZoneId())).toLocalDate();
			LocalTime time = LocalTime.now().plusMinutes(1);

			ZonedDateTime zonedDateTime = ZonedDateTime.of(date, time,
					ZoneId.of(requestReminder.getZoneId()));
			EmailSchedule oldEmailSchedule = emailScheduleRepositroy.findByRequestReminderId(requestReminder.getRequestReminderId());
			String emailJob = oldEmailSchedule.getJobName();
			String emailGroup = oldEmailSchedule.getJobName();;
			if (zonedDateTime.isBefore(ZonedDateTime.now())) {
				EmailResponse emailResponse = new EmailResponse(false, "DateTime must be after the current time");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emailResponse);
			}
			JobDetail jobDetail = buildJobDetail(requestReminder,emailJob,emailGroup);
			Trigger trigger = buildTrigger(jobDetail, zonedDateTime, requestReminder.getDaysToRemind(),
					requestReminder.getEndDate(), emailGroup);
			rescheduleOldJob(oldTrigger.getKey(),trigger);
			
			EmailSchedule emailSchedule = new EmailSchedule(requestReminder.getRequestId(),
					requestReminder.getRequestReminderId(), Date.from(requestReminder.getStartDate().toInstant()),
					Date.from(requestReminder.getEndDate().toInstant()), emailJob, emailGroup, trigger.toString(),
					requestReminder.getDaysToRemind(), trigger.getJobKey().toString());
			oldEmailSchedule = modelMapper.map(emailSchedule, EmailSchedule.class);
			emailScheduleRepositroy.save(oldEmailSchedule);
			EmailResponse emailResponse = new EmailResponse(true, jobDetail.getKey().getName(),
					jobDetail.getKey().getGroup(), "Email scheduled sucessfully");
			return  ResponseEntity.status(HttpStatus.ACCEPTED).body(emailResponse);
		} catch (Exception e) {
			log.error("Exception while re-scheduling email", e);	
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new EmailResponse(false,
				"Error while re-scheduling email, Please tryt again later"));
	}
	public JobDetail buildJobDetail(RequestReminders requestReminder,String emailJob, String emailGroup) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put("toEmail", requestReminder.getToEmail());
		jobDataMap.put("fromEmail", requestReminder.getFromEmail());
		jobDataMap.put("daysToRemind", requestReminder.getDaysToRemind());
		jobDataMap.put("emailSubject", requestReminder.getEmailSubject());
		jobDataMap.put("emailBody", requestReminder.getEmailBody());

		return JobBuilder.newJob(EmailJob.class)
				 .withIdentity(emailJob, emailGroup)
				.withDescription("Job to send reminder emails").usingJobData(jobDataMap).storeDurably().build();
	}

	public Trigger buildTrigger(JobDetail jobDetail, ZonedDateTime startAt, Long intervalInDays, Date endDate,
			String emailGroup) {
		int intervalInHours = Math.toIntExact(intervalInDays) * 24;
		return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName(), emailGroup)
				.startAt(Date.from(startAt.toInstant()))
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(intervalInHours).repeatForever())
				.endAt(Date.from(endDate.toInstant())) // Corrected variable name to endDate
				.build();
	}

	public EmailResponse deleteScheduler(EmailSchedule emailSchedule) {
		try {
			JobKey jobKey = JobKey.jobKey(emailSchedule.getJobName(),emailSchedule.getGroupName());
			boolean deleteJob = scheduler.deleteJob(jobKey);
			if (deleteJob) {
				return new EmailResponse(true, "Scheduled dose not exist");
			}else {
				return new EmailResponse(true, "");
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return new EmailResponse(false, "Exception while deleting scheduled job");
	}

	public EmailResponse updateScheduler(EmailSchedule emailSchedule) {
		try {
			String job = (emailSchedule.getJobKey());
			String string = job.substring(8, job.length());
			JobKey jobKey = JobKey.jobKey(string);
			boolean deleteJob = scheduler.deleteJob(jobKey);
			if (deleteJob) {
				return new EmailResponse(true, "Scheduled job deleted successfully");
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return new EmailResponse(false, "Exception while deleting scheduled job");
	}

	public ResponseEntity<EmailResponse> rescheduleOldJob(TriggerKey triggerKey, Trigger newTrigger) {
		try {
			scheduler.rescheduleJob(triggerKey, newTrigger);
			return ResponseEntity.ok(new EmailResponse(true, "Job rescheduled successfully"));
		} catch (SchedulerException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new EmailResponse(false, "Exception while rescheduling job"));
		}
	}
}
