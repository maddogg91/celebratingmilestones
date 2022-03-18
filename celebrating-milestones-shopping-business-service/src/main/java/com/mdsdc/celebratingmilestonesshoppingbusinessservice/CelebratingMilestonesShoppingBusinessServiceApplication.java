package com.mdsdc.celebratingmilestonesshoppingbusinessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CelebratingMilestonesShoppingBusinessServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CelebratingMilestonesShoppingBusinessServiceApplication.class, args);
	}

}
