//数据分析控制器，提供销售统计、报表分析等数据接口
package com.example.demo.controller;

// 导入类的方法，要求方法与类名一致。
import com.example.demo.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 这个是spring专门处理HTTP请求的注释，告诉spring处理然后以json的形式返回
@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    // 这个是自动匹配接口的注释
    // 用于自动将spring的bean注入相匹配的接口中
    @Autowired
    private AnalysisService analysisService;

    // 这是一个处理HTTP的GET请求注释，匹配HTTP路径中的变量,然后根据
    // GET 请求查询内容，调用server的方法，然后自动转化为json
    @GetMapping("/subscription-stats")
    public List<Map<String, Object>> getSubscriptionStats() {
        return analysisService.getSubscriptionStats();
    }

    // Getmaping是处理HTTP GET的请求的一个方法
    // 当访问某一个路径中含有括号里面的关键词时，就调用这个方法
    // 返回值自动转json。
    @GetMapping("/customer-behavior")
    public List<Map<String, Object>> getCustomerBehavior() {
        return analysisService.getCustomerBehaviorAnalysis();
    }

    // 寻找存货周转列表
    @GetMapping("/inventory-turnover")
    public List<Map<String, Object>> getInventoryTurnover() {
        return analysisService.getInventoryTurnoverStats();
    }
    // 业务报表
    @GetMapping("/business-report")
    public Map<String, Object> getBusinessReport() {
        return analysisService.getBusinessReport();
    }
}
