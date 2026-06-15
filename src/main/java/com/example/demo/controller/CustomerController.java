package com.example.demo.controller;

import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @Autowired //让 Spring 自动帮你把依赖的对象拿过来用，让spring自动对接这一个接口的实现类，CustomerService是一个接口，Spring会自动找到它的实现类CustomerServiceImpl，并注入到这个控制器中。
    private CustomerService customerService;
    
    @GetMapping // 处理GUET的请求
    //GET 请求是 HTTP 协议中的一种方法，用来从服务器「获取」数据。
    public List<Customer> getAll() {
        // 将其返回业务层的getAll方法的结果，
        // getAll方法会调用数据访问层（Mapper）来查询数据库中的所有客户记录，并返回一个包含所有客户对象的列表。
        // 相当于连接了Serice层
        return customerService.getAll();
    }
    
    
    @GetMapping("/{id}") //处理id字段的GUET请求
    public Customer getById(@PathVariable Integer id) {
        return customerService.getById(id);
    }
    

    @PostMapping // 处理POST请求（相当于处理新增数据）
    public int add(@RequestBody Customer customer) {
        return customerService.add(customer);
    }
    

    @PutMapping("/{id}") // 处理PUT请求（相当于处理修改更新数据）
    public int update(@PathVariable Integer id, @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.update(customer);
    }
    
    @DeleteMapping("/{id}") // 处理DELETE请求（相当于处理删除数据）
    public int delete(@PathVariable Integer id) {
        return customerService.delete(id);
    }
}