package com.example.demo.service;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    // 报纸订阅情况统计
    List<Map<String, Object>> getSubscriptionStats();
    // 客户订阅行为分析
    List<Map<String, Object>> getCustomerBehaviorAnalysis();
    // 库存周转率统计
    List<Map<String, Object>> getInventoryTurnoverStats();
    // 业务报表
    Map<String, Object> getBusinessReport();
}
