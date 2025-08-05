package com.service.racertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RacertrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RacertrackerApplication.class, args);
	}

}
