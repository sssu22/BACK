package com.example.trendlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableScheduling
public class TrendlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrendlogApplication.class, args);
	}

}
