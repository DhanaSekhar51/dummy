package com.example.ServiceA.exception.model;

import org.springframework.stereotype.Component;

@Component
public class GlobalException {
	
	public Object fallBackMethod() {
		return "Service is temporarly unavailable from global exception";
	}

}
