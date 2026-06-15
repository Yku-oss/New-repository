package com.example.demo.service.impl;

import com.example.demo.entity.Newspaper;
import com.example.demo.mapper.NewspaperMapper;
import com.example.demo.service.NewspaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NewspaperServiceImpl implements NewspaperService {
    
    @Autowired
    private NewspaperMapper newspaperMapper;
    
    @Override
    public List<Newspaper> getAll() {
        return newspaperMapper.findAll();
    }
    
    @Override
    public Newspaper getById(Integer id) {
        return newspaperMapper.findById(id);
    }
    
    @Override
    public int add(Newspaper newspaper) {
        return newspaperMapper.insert(newspaper);
    }
    
    @Override
    public int update(Newspaper newspaper) {
        return newspaperMapper.update(newspaper);
    }
    
    @Override
    public int delete(Integer id) {
        return newspaperMapper.deleteById(id);
    }
}