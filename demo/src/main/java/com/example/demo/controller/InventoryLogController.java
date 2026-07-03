//库存日志控制器，处理库存变动记录相关的HTTP请求
package com.example.demo.controller;

import com.example.demo.entity.InventoryLog;
import com.example.demo.service.InventoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-logs")
public class InventoryLogController {

    @Autowired
    private InventoryLogService inventoryLogService;

    @GetMapping
    public List<InventoryLog> getAll() {
        System.out.println("有人访问了库存日志列表！");
        return inventoryLogService.getAll();
    }
    //GetMapping注解表示当客户端发送一个HTTP GET请求到/api/inventory-logs路径时，getAll方法将被调用来处理这个请求
    // 这个方法会调用Service层的getAll方法来查询所有的库存日志信息，并将其返回给前端进行展示
    
    @GetMapping("/newspaper/{newspaperId}")
    public List<InventoryLog> getByNewspaper(@PathVariable Integer newspaperId) {
        return inventoryLogService.getByNewspaperId(newspaperId);
    }
}
