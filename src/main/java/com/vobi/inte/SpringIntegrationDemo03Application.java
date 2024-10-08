package com.vobi.inte;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class SpringIntegrationDemo03Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringIntegrationDemo03Application.class, args);
	}

}
