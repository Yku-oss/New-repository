//客户业务接口，定义客户相关业务逻辑方法
package com.example.demo.service;

import com.example.demo.entity.Customer;
import java.util.List;

public interface CustomerService {
    List<Customer> getAll();
    Customer getById(Integer id);
    Customer getByEmail(String email);
    Customer login(String email, String password);
    int add(Customer customer);
    int update(Customer customer);
    int delete(Integer id);
}