package com.example;

import com.example.application.config.DataSeeder;
import com.example.application.config.DataSeeder2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		DataSeeder.main(null);
	}

}
