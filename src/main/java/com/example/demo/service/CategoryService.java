package com.example.demo.service;

import com.example.demo.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAll();
    Category getById(Integer id);
    int add(Category category);
    int update(Category category);
    int delete(Integer id);
}
