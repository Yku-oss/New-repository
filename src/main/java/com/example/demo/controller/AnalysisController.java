package com.example.demo.controller;

import com.example.demo.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/subscription-stats")
    public List<Map<String, Object>> getSubscriptionStats() {
        return analysisService.getSubscriptionStats();
    }

    @GetMapping("/customer-behavior")
    public List<Map<String, Object>> getCustomerBehavior() {
        return analysisService.getCustomerBehaviorAnalysis();
    }

    @GetMapping("/inventory-turnover")
    public List<Map<String, Object>> getInventoryTurnover() {
        return analysisService.getInventoryTurnoverStats();
    }

    @GetMapping("/business-report")
    public Map<String, Object> getBusinessReport() {
        return analysisService.getBusinessReport();
    }
}
