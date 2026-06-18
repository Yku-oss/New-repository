//报纸管理控制器，处理报纸相关的HTTP请求（增删改查、库存管理等）
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
        System.out.println("有人访问了报纸列表！");
        return newspaperService.getAll();
    }
    
    @GetMapping("/{id}")
    public Newspaper getById(@PathVariable Integer id) {
        return newspaperService.getById(id);
    }

    @GetMapping("/category/{categoryId}")
    public List<Newspaper> getByCategory(@PathVariable Integer categoryId) {
        return newspaperService.getByCategoryId(categoryId);
    }

    @GetMapping("/recommended")
    public List<Newspaper> getRecommended() {
        return newspaperService.getRecommended();
    }

    @GetMapping("/low-stock")
    public List<Newspaper> getLowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        return newspaperService.getLowStock(threshold);
    }
    // @postmapping 是一个处理HTTP post请求的一个spring注解
    // 表示 当客户端发送一个HTTP POST请求到/api/newspapers路径时，add方法将被调用来处理这个请求
    // 就相当于打开一个网站接口，或者一个添加/登录/删除的接口
    // 前端就会把这个HTTP网页 POST请求传输到后端，后端就会调用这个add方法来处理这个请求
    // @RequestBody注解表示从HTTP请求的请求体中获取数据，
    // 并将其转换为Newspaper对象，方便后续的业务处理
    @PostMapping
    public int add(@RequestBody Newspaper newspaper) {
        return newspaperService.add(newspaper);
    }
    
    @PutMapping("/{id}")
    public int update(@PathVariable Integer id, @RequestBody Newspaper newspaper) {
        newspaper.setId(id);
        return newspaperService.update(newspaper);
    }

    @PutMapping("/{id}/stock")
    public int updateStock(@PathVariable Integer id, @RequestParam Integer quantity) {
        return newspaperService.updateStock(id, quantity);
    }
    
    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return newspaperService.delete(id);
    }
}