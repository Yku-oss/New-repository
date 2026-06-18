//订阅业务接口，定义订阅相关业务逻辑方法
package com.example.demo.service;

import com.example.demo.entity.Subscription;
import java.util.List;

public interface SubscriptionService {
    List<Subscription> getAll();
    Subscription getById(Integer id);
    List<Subscription> getByCustomerId(Integer customerId);
    List<Subscription> getByApprovalStatus(String approvalStatus);
    int add(Subscription subscription);
    int cancel(Integer id);
    int approve(Integer id);
    int reject(Integer id);
    int pay(Integer id, String paymentMethod);
}