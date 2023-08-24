package com.main.model;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "REQUEST_REMINDERS")
public class RequestReminders implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REQUEST_REMINDERS_S")
	@SequenceGenerator(sequenceName = "REQUEST_REMINDERS_S", allocationSize = 1, name = "REQUEST_REMINDERS_S")
	private Long requestReminderId;
    private Long requestId;
    private String toEmail;
    private String fromEmail;
    private Long daysToRemind;
    private String emailSubject;
    private String emailBody;
    private Date startDate;
    private Date endDate;
    private String zoneId;
}