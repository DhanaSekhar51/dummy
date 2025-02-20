package com.example.ServiceA.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter{
	
	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Autowired
	JWTUtils jwtUtils;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		logger.info("Auth Token Filter called for URI.. ", request.getRequestURI());
		
		try {
			String jwt = parseJwt(request);
			if(jwt != null && jwtUtils.validateJWTToken(jwt)) {
				
				String usernameFromToken = jwtUtils.getUsernameFromToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);
				
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				logger.info("Authorties... ", userDetails.getAuthorities());
				
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
				
			}
			
		}catch(Exception e) {
			logger.info("error in auth filter... ", e.getMessage());
		}
		
		filterChain.doFilter(request, response);
	}


	private String parseJwt(HttpServletRequest request) {
		
		String jwt = jwtUtils.getJWTFromHeader(request);
		logger.info("Token... ", jwt);
		
		return jwt;
	}

}
