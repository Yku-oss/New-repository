package org.example.demo777.Mapper;

import org.example.demo777.entity.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface seckillMapeer {
    // 查找商品id
    @Select("SELECT * FROM seckill.seckill_goods WHERE id = #{id}")
    goods selectById(@Param("id") Long id);

    // 对数据库进行更新修改
    // 如果已经更新，版本已经不同，就可以直不予理会，如果还是有差别，那就更新，乐观锁，防止超卖，版本控制
    @Update("update seckill.seckill_goods set stock = stock - 1,version = version + 1 " + "where id = #{id} and stock > 0")
    int updateStock(@Param("id") Long id);

    // 防止ABA场景，两边互相更改 ，使用sql的原子性，如果version不匹配就直接不执行或者重试
    @Update("UPDATE seckill.seckill_goods SET stock = stock - 1, version = version + 1 " +
            "WHERE id = #{id} AND stock > 0 AND version = #{version}")
    int deductStockByVersion(@Param("id") Long id, @Param("version") Integer version);


}