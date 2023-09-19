package com.tfs.order.mgmt.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is responsible to start the Customer Microservice and enable the
 * following functionality :
 * <ol>
 * 		<li><b>@EnableAutoConfiguration:</b> Enable Spring Boot’s auto-configuration mechanism</li>
 * 		<li><b>@ComponentScan:</b> Enable @Component scan on the package where the application is located</li> 
 * 		<li><b>@Configuration:</b> Allow to register extra beans in the context or import additional configuration classes</li>
 * <ol/>
 * 
 *
 */
@SpringBootApplication
public class CustomerServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}
	
}