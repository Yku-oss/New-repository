package org.example.demo777.Controller;

import org.example.demo777.Sever.seckillSever;
import org.example.demo777.common.Result;
import org.springframework.boot.ApplicationArguments;
import org.example.demo777.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seckill")
public class seckillController {
    private final seckillSever seckillSever;

    public seckillController(seckillSever seckillSever) {
       this.seckillSever  = seckillSever;
    }

    // 实施秒杀系统，下单是写入，所以用PostMapping
    @PostMapping("/do")
    public Result<?> seckill(@RequestParam Long userId, @RequestParam Long goods_id){
        return seckillSever.doseckill(userId,goods_id);
    }

    @PostMapping("/Redis")
    public Result<?> redis(@RequestParam Long userId, @RequestParam Long goods_id){
        return seckillSever.doSeckillByRedis(userId,goods_id);
    }


}
