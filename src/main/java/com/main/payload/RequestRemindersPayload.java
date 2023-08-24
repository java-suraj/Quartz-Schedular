package com.main.payload;

import java.time.ZoneId;
import java.util.Date;

import lombok.Data;
@Data
public class RequestRemindersPayload {
	private Long requestReminderId;
    private Long requestId;
    private String toEmail;
    private String fromEmail;
    private Integer daysToRemind;
    private String emailSubject;
    private String emailBody;
    private Date startDate;
    private Date endDate;
    private ZoneId zoneId;
}
