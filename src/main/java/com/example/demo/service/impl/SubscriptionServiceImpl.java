package com.example.demo.service.impl;

import com.example.demo.entity.InventoryLog;
import com.example.demo.entity.Newspaper;
import com.example.demo.entity.Subscription;
import com.example.demo.mapper.InventoryLogMapper;
import com.example.demo.mapper.NewspaperMapper;
import com.example.demo.mapper.SubscriptionMapper;
import com.example.demo.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    
    @Autowired
    private SubscriptionMapper subscriptionMapper;
    
    @Autowired
    private NewspaperMapper newspaperMapper;

    @Autowired
    private InventoryLogMapper inventoryLogMapper;
    
    @Override
    public List<Subscription> getAll() {
        return subscriptionMapper.findAll();
    }
    
    @Override
    public Subscription getById(Integer id) {
        return subscriptionMapper.findById(id);
    }

    @Override
    public List<Subscription> getByCustomerId(Integer customerId) {
        return subscriptionMapper.findByCustomerId(customerId);
    }

    @Override
    public List<Subscription> getByApprovalStatus(String approvalStatus) {
        return subscriptionMapper.findByApprovalStatus(approvalStatus);
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
        subscription.setOrderTime(LocalDateTime.now());
        subscription.setPaymentStatus("未支付");
        subscription.setApprovalStatus("待审批");
        subscription.setStatus("待处理");
        return subscriptionMapper.insert(subscription);
    }
    
    @Override
    public int cancel(Integer id) {
        return subscriptionMapper.updateStatus(id, "已取消");
    }

    @Override
    public int approve(Integer id) {
        subscriptionMapper.updateApprovalStatus(id, "已通过");
        // 审批通过后扣减库存
        Subscription sub = subscriptionMapper.findById(id);
        if (sub != null) {
            newspaperMapper.updateStock(sub.getNewspaperId(), -sub.getQuantity());
            InventoryLog log = new InventoryLog();
            log.setNewspaperId(sub.getNewspaperId());
            log.setChangeQuantity(sub.getQuantity());
            log.setType("OUT");
            log.setRemark("订单#" + id + "审批通过，出库");
            inventoryLogMapper.insert(log);
        }
        return subscriptionMapper.updateStatus(id, "已通过");
    }

    @Override
    public int reject(Integer id) {
        subscriptionMapper.updateApprovalStatus(id, "已驳回");
        return subscriptionMapper.updateStatus(id, "已驳回");
    }

    @Override
    public int pay(Integer id, String paymentMethod) {
        subscriptionMapper.updatePaymentStatus(id, "已支付");
        return subscriptionMapper.updateStatus(id, "已支付");
    }
}