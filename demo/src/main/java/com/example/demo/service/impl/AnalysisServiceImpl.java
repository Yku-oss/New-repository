//分析业务实现类，实现销售统计、报表分析等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.mapper.SubscriptionMapper;
import com.example.demo.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Override
    public List<Map<String, Object>> getSubscriptionStats() {
        // 这里使用原始SQL查询统计数据
        // 由于使用MyBatis注解，直接在Mapper中定义统计查询
        return subscriptionMapper.getSubscriptionStats();
    }

    @Override
    public List<Map<String, Object>> getCustomerBehaviorAnalysis() {
        return subscriptionMapper.getCustomerBehaviorAnalysis();
    }

    @Override
    public List<Map<String, Object>> getInventoryTurnoverStats() {
        return subscriptionMapper.getInventoryTurnoverStats();
    }

    @Override
    public Map<String, Object> getBusinessReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("subscriptionStats", getSubscriptionStats());
        report.put("customerBehavior", getCustomerBehaviorAnalysis());
        report.put("inventoryTurnover", getInventoryTurnoverStats());
        return report;
    }
}
