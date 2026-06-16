package com.example.demo.service.impl;

import com.example.demo.entity.InventoryLog;
import com.example.demo.mapper.InventoryLogMapper;
import com.example.demo.service.InventoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryLogServiceImpl implements InventoryLogService {

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Override
    public List<InventoryLog> getAll() {
        return inventoryLogMapper.findAll();
    }

    @Override
    public List<InventoryLog> getByNewspaperId(Integer newspaperId) {
        return inventoryLogMapper.findByNewspaperId(newspaperId);
    }

    @Override
    public int add(InventoryLog inventoryLog) {
        return inventoryLogMapper.insert(inventoryLog);
    }
}
