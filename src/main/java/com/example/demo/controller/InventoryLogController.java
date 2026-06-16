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
        return inventoryLogService.getAll();
    }

    @GetMapping("/newspaper/{newspaperId}")
    public List<InventoryLog> getByNewspaper(@PathVariable Integer newspaperId) {
        return inventoryLogService.getByNewspaperId(newspaperId);
    }
}
