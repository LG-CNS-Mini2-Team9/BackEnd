package com.team9.question_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class QuestionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuestionServiceApplication.class, args);
	}

}
