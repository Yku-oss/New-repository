//付款管理控制器，处理付款相关的HTTP请求
package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.getAll();
    }

    // 根据付款的Id查询付款信息
    // @PathVariable注解用于从URL路径中提取变量值，这里表示从URL路径中的{id}位置提取付款的Id，并将其传递给getById方法进行处理
    @GetMapping("/{id}")
    public Payment getById(@PathVariable Integer id) {
        return paymentService.getById(id); //调用server层的getById方法来查询付款信息
        // 再将信息返回给前端进行展示
    }

    @GetMapping("/subscription/{subscriptionId}")
    public Payment getBySubscription(@PathVariable Integer subscriptionId) {
        return paymentService.getBySubscriptionId(subscriptionId);
    }

    @PostMapping
    public int add(@RequestBody Payment payment) {
        return paymentService.add(payment);
    }
}
