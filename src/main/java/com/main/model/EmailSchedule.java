package com.main.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.quartz.JobKey;
import org.quartz.Trigger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "EMAIL_SCHEDULE")
@AllArgsConstructor
@NoArgsConstructor
public class EmailSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMAIL_SCHEDULE_S")
	@SequenceGenerator(sequenceName = "EMAIL_SCHEDULE_S", allocationSize = 1, name = "EMAIL_SCHEDULE_S")
	private Long scheduleId;
	private Long requestId;
	private Long requestReminderId;
	private String requestName;
	private Date startAt;
	private Date endTime;
	private String jobName;
	private String groupName;
	private String triggerName;
	private String jobKey;
	private String cronExpr;
	private Integer scheduleInterval;
	private Long createdBy;
	private Date creationDate;
	private Long updatedBy;
	private Date updatedDate;
	private String scheduleName;
	private String scheduleType;
	private Long nextFireTime;
	private String scheduleStatus;

	public EmailSchedule(Long requestId, Long requestReminderId, Date startAt, Date endTime, String jobName,
			String groupName, String triggerName, Long nextFireTime,String jobKey) {
		this.requestId = requestId;
		this.requestReminderId = requestReminderId;
		this.startAt = startAt;
		this.endTime = endTime;
		this.jobName = jobName;
		this.groupName = groupName;
		this.triggerName = triggerName;
		this.nextFireTime = nextFireTime;
		this.jobKey = jobKey;
	}

}
