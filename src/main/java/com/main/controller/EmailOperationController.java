package com.main.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.main.payload.EmailResponse;
import com.main.payload.RequestRemindersPayload;
import com.main.service.RequestRemindersService;


@RestController
public class EmailOperationController {
	
	@Autowired
	private RequestRemindersService requestRemindersService;
	private static final String homeUrl = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"><title>My Home Page</title><style>body{font-family:Arial,sans-serif;margin:0;padding:0}header{background-color:#333;color:#fff;text-align:center;padding:10px}nav{background-color:#444;color:#fff;text-align:center;padding:10px}nav a{color:#fff;text-decoration:none;margin:0 10px}nav a:hover{color:#f00}main{padding:20px}footer{background-color:#333;color:#fff;text-align:center;padding:10px;position:absolute;bottom:0;width:100%}</style></head><body><header><h1>Welcome to My Home Page</h1></header><nav><a href=\"#\">Home</a><a href=\"#\">About</a><a href=\"#\">Services</a><a href=\"#\">Contact</a></nav><main><h2>About Us</h2><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non bibendum velit. Integer commodo ex ut hendrerit interdum. Vestibulum lacinia libero vel dolor lacinia, non mattis lectus ultrices.</p><h2>Our Services</h2><ul><li>Service 1</li><li>Service 2</li><li>Service 3</li></ul><h2>Contact Us</h2><p>Email: contact@example.com</p><p>Phone: +1 123-456-7890</p></main><footer><p>&copy; 2023 My Home Page. All rights reserved.</p></footer></body></html>\r\n";
	
	@PostMapping("/addRequestReminder")
	public List<EmailResponse> addXxaasRequestRemainders(
			@RequestBody @Valid List<RequestRemindersPayload> xxaasRequestRemindersDto) {
		return requestRemindersService.addRequestRemainders(xxaasRequestRemindersDto);
	}
	
	@PutMapping("/updateRequestReminder")
	public List<ResponseEntity<EmailResponse>> updateRequestReminder(
			@RequestBody @Valid List<RequestRemindersPayload> xxaasRequestRemindersDto) {
		return requestRemindersService.updateRequestReminder(xxaasRequestRemindersDto);
	}
	
	@GetMapping("/welcome")
	public ResponseEntity<String> homePage(){
		return ResponseEntity.ok(homeUrl);
	}
	
	@DeleteMapping("/deleteSchedule")
	public List<EmailResponse> deleteSchedule(@RequestBody List<Long> requestReminderId) {
		 return requestRemindersService.deleteSchedule(requestReminderId);
		
	}
}
