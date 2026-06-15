package com.example.demo.service.impl;

import com.example.demo.entity.Customer;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


// 这是一个Spring Boot项目框架中的一个Service层的实现类，用于实现Customer相关的业务逻辑。
// 它实现了CustomerService接口中的方法，提供了具体的业务逻辑实现
// 通过@Autowired注解自动注入了CustomerMapper实例，
// CustomerMapper是一个MyBatis的Mapper接口，用于执行数据库操作。
@Service //这是Spring 框架中的一个注解
// 用来表示这是Service层的组件，Spring会自动将这个类注册为一个Bean，
// 并且可以在其他地方通过@Autowired注解来注入使用。
public class CustomerServiceImpl implements CustomerService {
    
    @Autowired  //自动注入CustomerMapper实例，CustomerMapper是一个MyBatis的Mapper接口，用于执行数据库操作。
    private CustomerMapper customerMapper;
    
    @Override // 实现CustomerService接口中的方法，提供具体的业务逻辑。作用是实现CustomerService接口中的方法，提供具体的业务逻辑。
    public List<Customer> getAll() {
        return customerMapper.findAll();
    }
    
    @Override // 这个还是表明 一个父亲类或者是一个接口中的一个方法重写。
    public Customer getById(Integer id) {
        return customerMapper.findById(id);
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