package com.example.demo.service;

import com.example.demo.entity.Newspaper;
import java.util.List;

public interface NewspaperService {
    List<Newspaper> getAll();
    Newspaper getById(Integer id);
    int add(Newspaper newspaper);
    int update(Newspaper newspaper);
    int delete(Integer id);
}