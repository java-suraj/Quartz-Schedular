package com.main.payload;

import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class EmailRequest {

	@Email
	@NotEmpty
	private String toEmail;

	@NotEmpty
	private String subject;

	@NotEmpty
	private String body;

	@NotNull
	private LocalDateTime localDateTime;

	@NotEmpty
	private ZoneId zoneId;
}
