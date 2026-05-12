package com.example.demo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import com.example.demo.Run.Location;
import com.example.demo.Run.RunRecord;
import com.example.demo.Services.OrderService;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private OrderService orderService;

	// @Autowired
	// public void setOrderService(OrderService orderService){
	// 	this.orderService = orderService;
	// }

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(DemoApplication.class, args);

		// System.out.println("=== BEAN REGISTRATI ===");
		// String[] beanNames = ctx.getBeanDefinitionNames();
		// for(String name : beanNames){
		// 	if(!name.contains("org.springframework"))
		// 	{
		// 		System.out.println(" - " + name);
		// 	}
		// }

		// WelcomeMessage msg = new WelcomeMessage();
		// System.out.println(msg.getMessage());


	}

	@Bean
	CommandLineRunner runner(OrderService orderService){
		return args -> {
			// RunRecord run = new RunRecord(124, 
			// 						"Corsa pomeridiana", 
			// 						LocalDateTime.now(), 
			// 						LocalDateTime.now().plus(1, ChronoUnit.HOURS),
			// 						5,
			// 						Location.INDOOR);
			// System.out.println("Run " + run);
			orderService.processOrder("ORD-001");
			orderService.processOrder("ORD-002");
		};
	}

}
