package com.example.trendlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TrendlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrendlogApplication.class, args);
	}

}
