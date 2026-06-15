package com.example.demo.service;

import com.example.demo.entity.Subscription;
import java.util.List;

public interface SubscriptionService {
    List<Subscription> getAll();
    Subscription getById(Integer id);
    int add(Subscription subscription);
    int cancel(Integer id);
    
}