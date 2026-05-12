package com.example.demo.Services;

import org.springframework.stereotype.Service;

@Service
public class SalutoService {
    public String saluta(String nome){
        return "Ciao, " + nome + "! Benvenuto nel sistema";
    }
}
