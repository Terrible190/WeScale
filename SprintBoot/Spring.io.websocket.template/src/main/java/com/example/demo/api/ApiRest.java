package com.example.demo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.api.model.User;

@RestController
public class ApiRest {
 
    /*Esto lo que hace és desde el navegador tengas que poner http://localhost:8080/hello?name=Usuari "hello es pot cambiar 
    en la linea d'avaix"*/
    @GetMapping("/hello")
    /*Els parametres son aquest, el valor per defecte es World*/
    public User hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        /*Retorna un json:  {"id":1,"name":"Hello Usuari!"} */
      return new User(1,String.format("Hello %s!", name));
    }
}