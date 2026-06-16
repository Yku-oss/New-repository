package com.example.demo.service;

import com.example.demo.entity.Payment;
import java.util.List;

public interface PaymentService {
    List<Payment> getAll();
    Payment getById(Integer id);
    Payment getBySubscriptionId(Integer subscriptionId);
    int add(Payment payment);
}
