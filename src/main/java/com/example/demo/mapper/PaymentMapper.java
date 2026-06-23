//付款数据访问接口，定义付款表的数据库操作
package com.example.demo.mapper;

import com.example.demo.entity.Payment;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PaymentMapper {
    
    @Select("SELECT * FROM payment")
    List<Payment> findAll();
    
    @Select("SELECT * FROM payment WHERE id = #{id}")
    Payment findById(@Param("id") Integer id);
    
    @Delete("DELETE FROM payment WHERE subscription_id IN (SELECT id FROM subscription WHERE newspaper_id = #{newspaperId})")
    int deleteByNewspaperId(@Param("newspaperId") Integer newspaperId);

    @Select("SELECT * FROM payment WHERE subscription_id = #{subscriptionId}")
    Payment findBySubscriptionId(@Param("subscriptionId") Integer subscriptionId);
    
    @Insert("INSERT INTO payment (subscription_id, amount, payment_method, transaction_id, status) " +
            "VALUES (#{subscriptionId}, #{amount}, #{paymentMethod}, #{transactionId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Payment payment);
    
    @Update("UPDATE payment SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
}
