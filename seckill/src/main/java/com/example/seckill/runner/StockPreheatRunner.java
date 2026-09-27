package com.example.seckill.runner;

import com.example.seckill.entity.SeckillGoods;
import com.example.seckill.mapper.SeckillGoodsMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class StockPreheatRunner implements CommandLineRunner {

    private final SeckillGoodsMapper goodsMapper;
    private final StringRedisTemplate redisTemplate;

    public StockPreheatRunner(SeckillGoodsMapper goodsMapper, StringRedisTemplate redisTemplate) {
        this.goodsMapper = goodsMapper;
        this.redisTemplate = redisTemplate;
    }
    // 构造器注入（写过的）

    // String... args是表示可变参数，将传入这些的参数进行包装，统一打包成args
    @Override
    public void run(String... args) {
        // 1. 从 DB 查所有商品
        List<SeckillGoods> goodsList = goodsMapper.selectAll();

        // 2. 遍历，把每个商品的库存写入 Redis
        for (SeckillGoods goods : goodsList) {
            // key 必须和 STOCK_LUA 里用的一致：seckill:stock:{goodsId}
            String key = "seckill:stock:" + goods.getId();
            // value 用字符串（StringRedisTemplate 只能存 String）
            String value = String.valueOf(goods.getStock());

            redisTemplate.opsForValue().set(key, value);
            System.out.println("【库存预热】" + key + " = " + value);
        }

        // 3. 汇总日志
        System.out.println("【库存预热】完成，共预热 " + goodsList.size() + " 个商品");
    }


}