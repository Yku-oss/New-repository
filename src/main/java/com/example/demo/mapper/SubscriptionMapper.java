//订阅数据访问接口，定义订阅表的数据库操作
package com.example.demo.mapper;

import com.example.demo.entity.Subscription;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface SubscriptionMapper {
    
    @Select("SELECT * FROM subscription")
    List<Subscription> findAll();
    
    @Select("SELECT * FROM subscription WHERE id = #{id}")
    Subscription findById(@Param("id") Integer id);
    
    @Select("SELECT * FROM subscription WHERE customer_id = #{customerId}")
    List<Subscription> findByCustomerId(@Param("customerId") Integer customerId);
    
    @Select("SELECT * FROM subscription WHERE approval_status = #{approvalStatus}")
    List<Subscription> findByApprovalStatus(@Param("approvalStatus") String approvalStatus);
    
    @Insert("INSERT INTO subscription (customer_id, newspaper_id, quantity, total_price, " +
            "start_date, end_date, delivery_address, payment_method, payment_status, approval_status, status) " +
            "VALUES (#{customerId}, #{newspaperId}, #{quantity}, #{totalPrice}, " +
            "#{startDate}, #{endDate}, #{deliveryAddress}, #{paymentMethod}, #{paymentStatus}, #{approvalStatus}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Subscription subscription);
    
    @Update("UPDATE subscription SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);
    
    @Update("UPDATE subscription SET approval_status = #{approvalStatus} WHERE id = #{id}")
    int updateApprovalStatus(@Param("id") Integer id, @Param("approvalStatus") String approvalStatus);
    
    @Update("UPDATE subscription SET payment_status = #{paymentStatus} WHERE id = #{id}")
    int updatePaymentStatus(@Param("id") Integer id, @Param("paymentStatus") String paymentStatus);
    
    @Delete("DELETE FROM subscription WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);

    @Delete("DELETE FROM subscription WHERE newspaper_id = #{newspaperId}")
    int deleteByNewspaperId(@Param("newspaperId") Integer newspaperId);

    // ===== 统计分析 =====
    @Select("SELECT n.name AS newspaperName, COUNT(s.id) AS subscriptionCount, SUM(s.total_price) AS totalRevenue " +
            "FROM subscription s JOIN newspaper n ON s.newspaper_id = n.id " +
            "GROUP BY n.name ORDER BY subscriptionCount DESC")
    List<Map<String, Object>> getSubscriptionStats();

    @Select("SELECT c.name AS customerName, c.phone, COUNT(s.id) AS orderCount, SUM(s.total_price) AS totalSpent " +
            "FROM subscription s JOIN customer c ON s.customer_id = c.id " +
            "GROUP BY c.id ORDER BY totalSpent DESC")
    List<Map<String, Object>> getCustomerBehaviorAnalysis();

    @Select("SELECT n.name AS newspaperName, n.stock, " +
            "COALESCE(SUM(CASE WHEN il.type='OUT' THEN il.change_quantity ELSE 0 END), 0) AS totalOut, " +
            "COALESCE(SUM(CASE WHEN il.type='IN' THEN il.change_quantity ELSE 0 END), 0) AS totalIn " +
            "FROM newspaper n LEFT JOIN inventory_log il ON n.id = il.newspaper_id " +
            "GROUP BY n.id ORDER BY totalOut DESC")
    List<Map<String, Object>> getInventoryTurnoverStats();
}