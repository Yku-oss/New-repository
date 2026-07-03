//客户管理控制器，处理客户相关的HTTP请求（增删改查、登录等）
package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @GetMapping
    public List<Customer> getAll() {
        return customerService.getAll();
    }
    
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Integer id) {
        return customerService.getById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        // 从请求体中获取登录数据，包括邮箱和密码
        String email = loginData.get("email");
        String password = loginData.get("password");
        // 调用Service层的login方法进行登录验证，返回一个Customer对象，
        // 如果登录成功则包含客户信息，否则为null
        Customer customer = customerService.login(email, password);
        Map<String, Object> result = new HashMap<>();
        if (customer != null) {
            result.put("success", true);
            result.put("customer", customer);
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "邮箱或密码错误");
            return ResponseEntity.ok(result);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Customer customer) {
        customer.setRole("customer");
        int res = customerService.add(customer);
        Map<String, Object> result = new HashMap<>();
        if (res > 0) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", "注册失败");
        }
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    public int add(@RequestBody Customer customer) {
        return customerService.add(customer);
    }
    
    @PutMapping("/{id}")
    public int update(@PathVariable Integer id, @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.update(customer);
    }
    
    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return customerService.delete(id);
    }
}