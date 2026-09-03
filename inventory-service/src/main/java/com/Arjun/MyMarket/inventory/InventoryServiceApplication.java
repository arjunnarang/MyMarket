package com.Arjun.MyMarket.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InventoryServiceApplication {

	public static void main(String[] args) {

		System.out.println("Default TimeZone = " +
				java.util.TimeZone.getDefault());

		System.out.println("Default ZoneId = " +
				java.time.ZoneId.systemDefault());

		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
