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