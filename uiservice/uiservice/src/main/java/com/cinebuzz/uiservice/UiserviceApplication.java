package com.cinebuzz.uiservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UiserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiserviceApplication.class, args);
		System.out.println("UIservice service started on port number 8761");
	}

}
