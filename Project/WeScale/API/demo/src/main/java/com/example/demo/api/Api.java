package com.example.demo.api;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Api {
    
	@GetMappint("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name){

		return String.format("Hello &s!",name);

	}
}
