package org.example.demo777.Mapper;

import org.springframework.format.annotation.DateTimeFormat;
import org.example.demo777.entity.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface orderMapper {
    @Select("SELECT * FROM seckill.seckill_order where user_id = #{user_id} and goods_id = #{goods_id}")
    Order getOrderById(@Param("user_id") Long user_id,@Param("goods_id")  Long goods_id);
    @Insert("INSERT INTO seckill.seckill_order (order_no, user_id, goods_id, status) " +
            "VALUES(#{orderNo}, #{userId}, #{goodsId}, #{status})")
    int insertOrder(Order order);
}
