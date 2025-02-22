package com.example.ServiceA.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.ServiceA.model.Employee;
import com.example.ServiceA.producer.MessageProducer;

@RestController
@RequestMapping("/ayncCheck")
public class FluxController {
	
//	@Autowired
//	MessageProducer producer;
	
	@Autowired
	KafkaTemplate<String, Object> template;

	//@PreAuthorize("hasRole('USER')")
	@GetMapping("/employeeData/{id}")
	public Employee getDataById(@PathVariable int id) {
		
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:8991").build();
		
		Employee empData = webClient.get().uri("/fetch/"+id).retrieve().bodyToMono(Employee.class).block();
		
		
		return empData;
	}
	
	//@PreAuthorize("hasRole('USER')")
	@PutMapping("/message/{name}")
	public String sendMessageDataById(@PathVariable String name) {
		
		System.out.println("name....  "+name);
		
		//producer.sendMessage("my-topic", String.valueOf(id));
		
		CompletableFuture<SendResult<String, Object>> send = template.send("java-kafka", name);	
		send.whenComplete((result, ex) -> {
			if(ex == null) {
				System.out.println("sent message "+name+" with offset"+result.getRecordMetadata().offset());
			}else {
				System.out.println("unable to send message "+ex.getMessage());
			}
		});
		return "Message sent successfully...";
	}
	
	@PostMapping("/message/eventCheck")
	public String sendMessageEvent(@RequestBody Employee emp) {
		
		System.out.println("name.....  "+emp.toString());
		
		//producer.sendMessage("my-topic", String.valueOf(id));
		
		try {
			CompletableFuture<SendResult<String, Object>> send = template.send("java-kafka-event1", emp);	
			send.whenComplete((result, ex) -> {
				if(ex == null) {
					System.out.println("sent message event"+emp+" with offset"+result.getRecordMetadata().offset());
				}else {
					System.out.println("unable to send message "+ex.getMessage());
				}
			});
		}catch(Exception e) {
			System.out.println("Exception while producing the message... "+e.getMessage()+" \n "+e.getCause());
		}
		
		
		return "Message sent successfully...";
	}
	
	@PostMapping("/message/retryCheck")
	public String sendMessageRetryEvent(@RequestBody Employee emp) {
		
		System.out.println("name....  "+emp.toString());
		
		template.send("retyrCheck", emp);
		
		
		return "Message sent successfully...";
	}
	
	@GetMapping("/message/kafkaPartition/{name}")
	public String sendKafkaPartiton(@PathVariable String name) {
		
		System.out.println("name....  "+name);
		
		//producer.sendMessage("my-topic", String.valueOf(id));
		
		try {
			
			for(int i=0;i<1000;i++) {
				//CompletableFuture<SendResult<String, Object>> send = template.send("kafkaPartition", 2, null, name+" "+i); // to produce the message to the respective partition
				CompletableFuture<SendResult<String, Object>> send = template.send("kafkaPartition", name+" "+i);
				send.whenComplete((result, ex) -> {
					if(ex == null) {
						System.out.println("sent message event"+name+" with offset"+result.getRecordMetadata().offset());
					}else {
						System.out.println("unable to send message "+ex.getMessage());
					}
				});
			}
		}catch(Exception e) {
			System.out.println("Exception while producing the message... "+e.getMessage()+" \n "+e.getCause());
		}
		
		
		return "Message sent successfully...";
	}
	
}
