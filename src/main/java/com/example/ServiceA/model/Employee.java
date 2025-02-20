package com.example.ServiceA.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

	private Integer empid;
	
	private String name;
	
	private String designation;
	
	private String ipAddress;

}
