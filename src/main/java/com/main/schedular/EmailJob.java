package com.main.schedular;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailJob implements Job {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailProperties mailProperties;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
		String subject = jobDataMap.getString("emailSubject");
		String body = jobDataMap.getString("emailBody");
		String toEmail = jobDataMap.getString("toEmail");
		//String fromEmail = jobDataMap.getString("fromEmail");

		// Here, you can retrieve email credentials or configuration from mailProperties
		String pass = mailProperties.getPassword();
		String fromEmail = mailProperties.getUsername();

		System.out.println("From email: " + fromEmail);
		System.out.println("To email: " + toEmail);
		//  Uncomment the line below if you have retrieved password from mailProperties
		System.out.println("Password: " + pass);

		sendMail(fromEmail, toEmail, subject, body);
		System.out.println("Email sent successfully.....!!!!");
	}

	private void sendMail(String fromEmail, String recipientEmail, String subject, String body) {
		try {
			// Implement your email sending logic here using mailSender or any other library
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
			messageHelper.setFrom(fromEmail);
			messageHelper.setTo(recipientEmail);
			mailSender.send(message);

			// For demonstration purposes, we are just printing the email details.
			System.out.println("Email Details:");
			System.out.println("Subject: " + subject);
			System.out.println("Body: " + body);
			System.out.println("From: " + fromEmail);
			System.out.println("To: " + recipientEmail);
		} catch (MailAuthenticationException ex) {
			// Handle authentication errors
			ex.printStackTrace();
			// Log or handle the error as appropriate
		} catch (MailSendException ex) {
			// Handle send failures (e.g., connection issues)
			ex.printStackTrace();
			// Log or handle the error as appropriate
		} catch (MessagingException ex) {
			// Handle other messaging-related exceptions
			ex.printStackTrace();
			// Log or handle the error as appropriate
		} catch (Exception ex) {
			// Catch any other unexpected exceptions
			ex.printStackTrace();
			// Log or handle the error as appropriate
		}
	}

	/*@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
	    JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
	    String subject = jobDataMap.getString("emailSubject");
	    String body = jobDataMap.getString("emailBody");
	    String toEmail = jobDataMap.getString("toEmail");
	    String fromEmail = mailProperties.getUsername();
	    String pass = mailProperties.getPassword();
	    System.out.println("From email: " + fromEmail);
	    System.out.println("To email: " + toEmail);
	    System.out.println("Password: " + pass);
	    sendMail(fromEmail, toEmail, subject, body);
	    System.out.println("Email sent successfully.....!!!!");
	}
	
	private void sendMail(String fromEmail, String recipientEmail, String subject, String body) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
	        messageHelper.setSubject(subject);
	        messageHelper.setText(body, true);
	        messageHelper.setFrom(fromEmail);
	        messageHelper.setTo(recipientEmail);
	        mailSender.send(message);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        System.out.println(ex);
	    }
	}*/
}