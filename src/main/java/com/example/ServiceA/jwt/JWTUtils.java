package com.example.ServiceA.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JWTUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);
	
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${spring.app.jwtExpirationMS}")
	private int jwtExpirationMS;
	
	public String getJWTFromHeader(HttpServletRequest request) {
		
		String header = request.getHeader("Authorization");
		logger.info("Bearer token ", header);
		if(header != null && header.startsWith("Bearer")) {
			return header.substring(7);
		}
		
		return null;
	}
	
	
	public String generatrTokenFromUsername(UserDetails userDetails) {
		
		String username = userDetails.getUsername();
		
		return Jwts.builder().setSubject(username)
			.setIssuedAt(new Date())
			.setExpiration(new Date((new Date()).getTime()+jwtExpirationMS))
			.signWith(key())
			.compact();
		
	}

	
	public String getUsernameFromToken(String token) {
		
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
		
	}

	private Key key() {
		// TODO Auto-generated method stub
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
	
	public boolean validateJWTToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
			return true;
		}catch(Exception e) {
			logger.info("Exception " + e.getMessage());
		}
		return false;
	}
	

}
