package com.example.demo.mapper;

import com.example.demo.entity.Subscription;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SubscriptionMapper {
    
    @Select("SELECT * FROM subscription")
    List<Subscription> findAll();
    
    @Select("SELECT * FROM subscription WHERE id = #{id}")
    Subscription findById(@Param("id") Integer id);
    
    @Insert("INSERT INTO subscription (customer_id, newspaper_id, quantity, total_price) VALUES (#{customerId}, #{newspaperId}, #{quantity}, #{totalPrice})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Subscription subscription);
    
    @Update("UPDATE subscription SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    @Delete("DELETE FROM subscription WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}