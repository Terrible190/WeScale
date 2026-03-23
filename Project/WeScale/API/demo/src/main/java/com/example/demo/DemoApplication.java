package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@RestController
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


/*	@GetMappint("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name){

		return String.format("Hello &s!",name);

	}*/

}


// https://start.spring.io/