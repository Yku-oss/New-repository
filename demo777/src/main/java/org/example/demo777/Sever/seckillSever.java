package org.example.demo777.Sever;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.demo777.Mapper.orderMapper;
import org.example.demo777.Mapper.seckillMapeer;
import org.example.demo777.common.Result;
import org.example.demo777.entity.Order;
import org.example.demo777.entity.goods;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class seckillSever {

    private final seckillMapeer seckillMapeer;
    private final orderMapper orderMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public seckillSever(seckillMapeer seckillMapeer, orderMapper orderMapper,
                        StringRedisTemplate stringRedisTemplate) {
        this.seckillMapeer = seckillMapeer;
        this.orderMapper = orderMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 秒杀下单（DB 版）：校验 → 一人一单 → 扣库存（防超卖）→ 落单
     */
    @Transactional
    public Result<?> doseckill(Long userId, Long goodsId) {
        // 1. 查商品
        goods goods = seckillMapeer.selectById(goodsId);
        if (goods == null) {
            return Result.error(404, "商品不存在");
        }

        // 2. 秒杀时间窗口（startTime/endTime）
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(goods.getStartTime()) || now.isAfter(goods.getEndTime())) {
            return Result.error(400, "不在秒杀时间窗口内");
        }

        // 3. 一人一单
        if (orderMapper.getOrderById(userId, goodsId) != null) {
            return Result.error(400, "每人限购一件");
        }

        // 4. 扣库存（DB 条件更新防超卖：stock > 0）
        int rows = seckillMapeer.updateStock(goodsId);
        if (rows == 0) {
            return Result.error(400, "库存不足");
        }

        // 5. 落单
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        order.setUserId(userId.intValue());
        order.setGoodsId(goodsId);
        order.setStatus(0);
        orderMapper.insertOrder(order);




        return Result.success("抢购成功");
    }
    private static  final String STOCK_LUA =
            "local stock = tonumber(redis.call('GET',KEYS[1]))" +"if sotck == nil then return -1 end " +
                    "if stock < tonumber(ARGV[1]) then return 0 end " +
                    "redis.call('DECRBY', KEYS[1], ARGV[1]) " +
                    "return 1";

    public Result<?> doSeckillByRedis(Long userId, Long goodsId) {
        Long result = stringRedisTemplate.execute(
                new DefaultRedisScript<>(STOCK_LUA, Long.class),   // "让 Redis 执行这段 Lua，返回数字"
                List.of("seckill:stock:" + goodsId),               // "脚本里 KEYS[1] 用这个 key"
                "1");
        if(result == -1){
            return Result.error(400, "活动未初始化");
        }
        if(result == null){
            return Result.error(500, "系统繁忙");
        }
        if (result == 0)  return Result.error(400, "已售罄");
        return Result.success("抢购成功");
    }
}
