//付款业务实现类，实现付款记录管理等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.entity.Payment;
import com.example.demo.mapper.PaymentMapper;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Override
    public List<Payment> getAll() {
        return paymentMapper.findAll();
    }

    @Override
    public Payment getById(Integer id) {
        return paymentMapper.findById(id);
    }

    @Override
    public Payment getBySubscriptionId(Integer subscriptionId) {
        return paymentMapper.findBySubscriptionId(subscriptionId);
    }

    @Override
    public int add(Payment payment) {
        return paymentMapper.insert(payment);
    }
}
