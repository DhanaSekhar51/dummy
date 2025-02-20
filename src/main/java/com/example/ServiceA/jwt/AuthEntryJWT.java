package com.example.ServiceA.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthEntryJWT implements AuthenticationEntryPoint{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthEntryJWT.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// TODO Auto-generated method stub
		logger.info("auth error... ", authException.getMessage());
		
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		map.put("error", "UnAuthorized");
		map.put("message", authException.getMessage());
		map.put("path", request.getServletPath());
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), map);
		
	}

}
