package com.vnayar.transaction;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionApplication.class, args);
	}

	// Injecting the clock allows us to more easily control it for unit testing.
	@Bean
	Clock getClock() {
		return Clock.systemUTC();
	}
}
