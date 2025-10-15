package com.payitforward.platform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
	@GetMapping("/api/test")
	public String test() {
		return "Pay It Forward Platform is Running Successfully! ";
	}
}
