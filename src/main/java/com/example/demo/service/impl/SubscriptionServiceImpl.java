package com.example.demo.service.impl;

import com.example.demo.entity.Newspaper;
import com.example.demo.entity.Subscription;
import com.example.demo.mapper.NewspaperMapper;
import com.example.demo.mapper.SubscriptionMapper;
import com.example.demo.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    
    @Autowired
    private SubscriptionMapper subscriptionMapper;
    
    @Autowired
    private NewspaperMapper newspaperMapper;
    
    @Override
    public List<Subscription> getAll() {
        return subscriptionMapper.findAll();
    }
    
    @Override
    public Subscription getById(Integer id) {
        return subscriptionMapper.findById(id);
    }
    
    @Override
    public int add(Subscription subscription) {
        // 根据报纸ID查询价格，计算总价
        Newspaper newspaper = newspaperMapper.findById(subscription.getNewspaperId());
        if (newspaper != null) {
            BigDecimal price = newspaper.getPrice();
            BigDecimal total = price.multiply(BigDecimal.valueOf(subscription.getQuantity()));
            subscription.setTotalPrice(total);
        }
        return subscriptionMapper.insert(subscription);
    }
    
    @Override
    public int cancel(Integer id) {
        return subscriptionMapper.updateStatus(id, "已取消");
    }
}