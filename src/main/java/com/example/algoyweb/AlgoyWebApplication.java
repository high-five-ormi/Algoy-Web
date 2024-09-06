package com.example.algoyweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlgoyWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlgoyWebApplication.class, args);
	}
}