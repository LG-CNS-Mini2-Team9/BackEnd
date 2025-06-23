package com.team9.ai_feedback_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// 임시
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AiFeedbackServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiFeedbackServiceApplication.class, args);
	}

}
