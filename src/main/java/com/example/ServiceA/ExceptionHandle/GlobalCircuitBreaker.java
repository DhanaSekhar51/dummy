package com.example.ServiceA.ExceptionHandle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.ServiceA.exception.model.GlobalException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Aspect
@Component
public class GlobalCircuitBreaker {
	
	private final CircuitBreaker circuitBreaker;
	private final GlobalException globalException;

	public GlobalCircuitBreaker(CircuitBreakerRegistry registry, GlobalException gException) {
		this.circuitBreaker = registry.circuitBreaker("Default");
		this.globalException = gException;
	}
	
	@Around("execution(* com.example..controller..*(..))")
	public Object applyCircuitBreaker(ProceedingJoinPoint joinPoint) throws Exception {
		
		return circuitBreaker.executeCallable(()->{
			try {
				return joinPoint.proceed();
			}catch(Throwable t) {
				return globalException.fallBackMethod();
			}
			
		});
		
	}

}
