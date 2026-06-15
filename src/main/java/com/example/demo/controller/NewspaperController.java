//这是一个Spring Boot控制器类，定义了一个RESTful API端点，返回一个简单的字符串响应。
// 相当于kafka ，接受数据，处理数据，发送数据
// 是控制处理请求与响应的类，是处理层的核心组件，负责处理HTTP请求并返回响应。
package com.example.demo.controller;

import com.example.demo.entity.Newspaper;
import com.example.demo.service.NewspaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/newspapers")
public class NewspaperController {
    
    @Autowired
    private NewspaperService newspaperService;
    
    @GetMapping
    public List<Newspaper> getAll() {
        return newspaperService.getAll();
    }
    
    @GetMapping("/{id}")
    public Newspaper getById(@PathVariable Integer id) {
        return newspaperService.getById(id);
    }
    
    @PostMapping
    public int add(@RequestBody Newspaper newspaper) {
        return newspaperService.add(newspaper);
    }
    
    @PutMapping("/{id}")
    public int update(@PathVariable Integer id, @RequestBody Newspaper newspaper) {
        newspaper.setId(id);
        return newspaperService.update(newspaper);
    }
    
    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return newspaperService.delete(id);
    }
}