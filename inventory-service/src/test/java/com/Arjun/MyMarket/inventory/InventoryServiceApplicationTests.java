package com.Arjun.MyMarket.inventory;

import com.Arjun.MyMarket.inventory.dto.ProductSnapshot;
import com.Arjun.MyMarket.inventory.external.ProductClient;
import org.hibernate.id.uuid.UuidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class InventoryServiceApplicationTests {

	@Autowired
	private ProductClient productClient;


    @Test
	void contextLoads() {
		System.out.println("Java timezone = " + java.util.TimeZone.getDefault());
		System.out.println("Java ZoneId = " + java.time.ZoneId.systemDefault());
	}


	@Test
	void getProductById(){
		System.out.println("Testing....");
		ProductSnapshot productSnapshot =  this.productClient.getProductById(UUID.fromString("04e0115d-9393-4e8f-8f18-ac087066e918"));
		System.out.println(productSnapshot);

	}
}
