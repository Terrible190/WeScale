package com.example.demo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.api.model.User;

@RestController
public class ApiRest {
 
    @GetMapping("/hello")
    public User hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return new User(1,String.format("Hello %s!", name));
    }
}