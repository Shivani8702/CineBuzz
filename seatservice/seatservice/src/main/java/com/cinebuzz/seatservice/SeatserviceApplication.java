package com.cinebuzz.seatservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SeatserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatserviceApplication.class, args);
		System.out.println("Seat server started on port number 8082");
	}
}
