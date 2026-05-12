package com.example.demo.Services;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    
    private final NotificationService notificationService;

    public OrderService (NotificationService notificationService){
        this.notificationService = notificationService;
    }

    public void processOrder(String OrderId){
        System.out.println("Elaborazione ordine: " + OrderId);
        notificationService.sendoConfirmation(OrderId);
    }
}
