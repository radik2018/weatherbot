package com.example.weatherbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class WeatherbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeatherbotApplication.class, args);
	}
}
