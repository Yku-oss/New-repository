//分类管理控制器，处理报纸分类相关的HTTP请求
package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories") //处理URL 问题，从路径中提取关键字，然后调用这个方法
public class CategoryController {

    @Autowired //spring自动化注释，将bean的依赖自动注入容器中
    private CategoryService categoryService;
    @GetMapping
    public List<Category> getAll() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Integer id) {
        return categoryService.getById(id); // api调用其他类的方法，返回ID
    }

    @PostMapping // HTTP 的post请求，表示增加某一变量和参数。
    public int add(@RequestBody Category category) {
        return categoryService.add(category);
    }

    @PutMapping("/{id}")
    // @PathVariable 是处理URL路径变量，通过提取这个变量，以此来调用这个方法
    // @RequestBoduy 是处理URL路径请求的注释，是提取这个路径的主体部分，调用方法类。
    public int update(@PathVariable Integer id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.update(category);
    }
    
    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }
}
