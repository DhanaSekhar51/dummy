package com.example.ServiceA.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.ServiceA.jwt.AuthEntryJWT;
import com.example.ServiceA.jwt.AuthTokenFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityCofig {
	
	@Autowired
	AuthEntryJWT unAuthorizationHandler;
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public AuthTokenFilter authenticationJWTFilter() {
		return new AuthTokenFilter();
	}
	
	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
		
		http.authorizeHttpRequests((request) ->
				request.requestMatchers("/h2-console/**","/ayncCheck/**").permitAll()
				.requestMatchers("/signin").permitAll()
				.anyRequest().authenticated());
			
		http.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.exceptionHandling(ex -> ex.authenticationEntryPoint(unAuthorizationHandler));
		http.addFilterBefore(authenticationJWTFilter(), UsernamePasswordAuthenticationFilter.class);
		http.csrf(csrf -> csrf.disable());
//		http.httpBasic(Customizer.withDefaults());
		http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));
		
		return http.build();
		
	}
	
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	
	@Bean
	public UserDetailsService userDetailService(DataSource dataSource) {
		return new JdbcUserDetailsManager(dataSource);
	}
	
	@Bean
	public CommandLineRunner initData(UserDetailsService userDetailsService) {
		
		return args -> {
			JdbcUserDetailsManager manager = (JdbcUserDetailsManager) userDetailsService;
			
			UserDetails user = User.withUsername("user")
					.password(passwordEncoder().encode("user@123"))
					.roles("USER")
					.build();
			
			UserDetails admin = User.withUsername("admin")
				.password(passwordEncoder().encode("admin@123"))
				.roles("ADMIN")
				.build();
			
			JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
			userDetailsManager.createUser(user);
			userDetailsManager.createUser(admin);
			
		};
		
		
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
