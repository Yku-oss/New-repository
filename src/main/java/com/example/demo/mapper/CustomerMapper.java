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

    @Select("SELECT * FROM customer WHERE email = #{email}")
    Customer findByEmail(@Param("email") String email);
    
    @Insert("INSERT INTO customer (name, phone, address, email, password, role) " +
            "VALUES (#{name}, #{phone}, #{address}, #{email}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);
    
    @Update("UPDATE customer SET name=#{name}, phone=#{phone}, address=#{address}, " +
            "email=#{email} WHERE id=#{id}")
    int update(Customer customer);
    
    @Delete("DELETE FROM customer WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}