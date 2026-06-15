package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    
    @Autowired
    private SubscriptionService subscriptionService;
    
    @GetMapping
    public List<Subscription> getAll() {
        return subscriptionService.getAll();
    }
    
    @GetMapping("/{id}")
    public Subscription getById(@PathVariable Integer id) {
        return subscriptionService.getById(id);
    }
    
    @PostMapping
    public int add(@RequestBody Subscription subscription) {
        return subscriptionService.add(subscription);
    }
    
    @PutMapping("/{id}/cancel")
    public int cancel(@PathVariable Integer id) {
        return subscriptionService.cancel(id);
    }
}