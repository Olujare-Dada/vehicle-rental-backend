package com.bptn.vehicle_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class VehicleProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleProjectApplication.class, args);
	}

}
