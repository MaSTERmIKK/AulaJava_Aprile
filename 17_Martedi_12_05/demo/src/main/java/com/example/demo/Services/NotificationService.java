package com.example.demo.Services;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    public void sendoConfirmation(String orderId){
        System.out.println("conferma inviata per ordine: " + orderId);
    }
}
