package com.team9.statistic_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableJpaRepositories
@EnableFeignClients(basePackages = "com.team9.statistic_service.remote")
@ComponentScan(basePackages = {"com.team9.statistic_service", "com.team9.common"})
public class StatisticServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatisticServiceApplication.class, args);
	}

}
