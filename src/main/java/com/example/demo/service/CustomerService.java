package com.example.demo.service;

import com.example.demo.entity.Customer;
import java.util.List;

// 这个是SPring Boot项目框架中的一个Service层，用于定义Customer相关的业务逻辑接口。
// 它定义了几个方法，包括获取所有客户、根据ID获取客户、添加客户、更新客户和删除客户。
// 这些方法将由具体的实现类来实现，通常会调用数据访问层（如Mapper）来执行数据库操作。
// 为Serviceimpl提供实现业务的接口方法，使得Controller层可以调用这些方法来处理HTTP请求，
// 并返回相应的结果。
public interface CustomerService {
    List<Customer> getAll();
    Customer getById(Integer id);
    int add(Customer customer);
    int update(Customer customer);
    int delete(Integer id);
}