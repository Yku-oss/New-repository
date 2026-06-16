package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    @GetMapping("/{id}")
    public Payment getById(@PathVariable Integer id) {
        return paymentService.getById(id);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public Payment getBySubscription(@PathVariable Integer subscriptionId) {
        return paymentService.getBySubscriptionId(subscriptionId);
    }

    @PostMapping
    public int add(@RequestBody Payment payment) {
        return paymentService.add(payment);
    }
}
