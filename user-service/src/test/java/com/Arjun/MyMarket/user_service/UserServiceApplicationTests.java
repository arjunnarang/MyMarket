package com.Arjun.MyMarket.user_service;

import com.Arjun.MyMarket.user_service.entity.User;
import com.Arjun.MyMarket.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	void contextLoads() {
	}

	@Test
	void showSavedEntity(){
		User user = new User();

		user.setName("Arjun");
		user.setEmail("abc@gmail.com");
		user.setPassword("abc@123");
		user.setAddress("abc new york 12");
		user.setPhoneNumber("323232323");

		System.out.println(user);

	}
}
