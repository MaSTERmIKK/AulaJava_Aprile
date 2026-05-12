package com.example.demo;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
// @Service
// @Repository
// @Controller
// @RestController
public class WelcomeMessage 
{
    private String msg;

    public String getMessage(){
        return msg;
    }

    @PostConstruct
    public void init(){
        msg = "Applicazione avviata con successo";
        System.out.println("[WelcomeMessage] Bean inizializzato");
    }

    @PreDestroy
    public void destroy(){

    }
    //LIFE CYCLE
    //INSTAZIAZIONE
    //INIEZIONE DELLE DIPENDENZE
    //@PostConstruct
    //USO
    //@PreDestroy
    //DISTRUZIONE
}
