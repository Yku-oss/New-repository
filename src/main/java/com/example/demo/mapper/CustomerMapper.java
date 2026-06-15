package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CustomerMapper {
    
    @Select("SELECT * FROM customer")
    List<Customer> findAll();
    
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer findById(@Param("id") Integer id);
    
    @Insert("INSERT INTO customer (name, phone, address) VALUES (#{name}, #{phone}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);
    
    @Update("UPDATE customer SET name=#{name}, phone=#{phone}, address=#{address} WHERE id=#{id}")
    int update(Customer customer);
    
    @Delete("DELETE FROM customer WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}