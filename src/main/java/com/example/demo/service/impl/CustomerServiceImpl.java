//客户业务实现类，实现客户增删改查及登录等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired
    private CustomerMapper customerMapper;
    
    @Override
    public List<Customer> getAll() {
        return customerMapper.findAll();
    }
    
    @Override
    public Customer getById(Integer id) {
        return customerMapper.findById(id);
    }

    @Override
    public Customer getByEmail(String email) {
        return customerMapper.findByEmail(email);
    }

    @Override
    public Customer login(String email, String password) {
        Customer customer = customerMapper.findByEmail(email);
        if (customer != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }
    
    @Override
    public int add(Customer customer) {
        return customerMapper.insert(customer);
    }
    
    @Override
    public int update(Customer customer) {
        return customerMapper.update(customer);
    }
    
    @Override
    public int delete(Integer id) {
        return customerMapper.deleteById(id);
    }
}