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

    @GetMapping("/customer/{customerId}")
    public List<Subscription> getByCustomer(@PathVariable Integer customerId) {
        return subscriptionService.getByCustomerId(customerId);
    }

    @GetMapping("/approval/{status}")
    public List<Subscription> getByApprovalStatus(@PathVariable String status) {
        return subscriptionService.getByApprovalStatus(status);
    }
    
    @PostMapping
    public int add(@RequestBody Subscription subscription) {
        return subscriptionService.add(subscription);
    }
    
    @PutMapping("/{id}/cancel")
    public int cancel(@PathVariable Integer id) {
        return subscriptionService.cancel(id);
    }

    @PutMapping("/{id}/approve")
    public int approve(@PathVariable Integer id) {
        return subscriptionService.approve(id);
    }

    @PutMapping("/{id}/reject")
    public int reject(@PathVariable Integer id) {
        return subscriptionService.reject(id);
    }

    @PutMapping("/{id}/pay")
    public int pay(@PathVariable Integer id, @RequestParam String paymentMethod) {
        return subscriptionService.pay(id, paymentMethod);
    }
}