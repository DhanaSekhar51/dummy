package com.example.ServiceA.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ServiceA.jwt.JWTUtils;
import com.example.ServiceA.model.LoginRequest;
import com.example.ServiceA.model.LoginResponse;

@RestController
public class UserCotroller {
	private static final Logger logger = LoggerFactory.getLogger(UserCotroller.class);
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	JWTUtils jwtUtils;

	@GetMapping("/hello")
	public String getHello() {
		
		return "hello";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user")
	public String getUser() {
		return "Hello User!";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/sampleExceptionCheck")
	public String getExceptionCheck() {
		System.out.println("condition check");
		String output = new RestTemplate().getForObject("http://localhost:8991/sampleCheck", String.class);
		
		return output;
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/admin")
	public String getAdmin() {
		return "Hello Admin!";
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request){
		
		logger.info("Auth user controller... ", request.getUsername());
		
		Authentication authentication;
		try {
			
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			
		}catch(AuthenticationException e) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("status", false);
			map.put("message", "Bad Credentials");
			return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
		}
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		String generatrTokenFromUsername = jwtUtils.generatrTokenFromUsername(userDetails);
		
		List<String> roles = userDetails.getAuthorities().stream().map(s->s.getAuthority()).collect(Collectors.toList());
		
		LoginResponse loginResponse = new LoginResponse(generatrTokenFromUsername, roles);
		
		return ResponseEntity.ok(loginResponse);
		
	}
	
}
