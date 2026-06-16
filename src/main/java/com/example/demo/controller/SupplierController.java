package com.example.demo.controller;

import com.example.demo.entity.Supplier;
import com.example.demo.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public List<Supplier> getAll() {
        return supplierService.getAll();
    }

    @GetMapping("/{id}")
    public Supplier getById(@PathVariable Integer id) {
        return supplierService.getById(id);
    }

    @PostMapping
    public int add(@RequestBody Supplier supplier) {
        return supplierService.add(supplier);
    }

    @PutMapping("/{id}")
    public int update(@PathVariable Integer id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        return supplierService.update(supplier);
    }

    @DeleteMapping("/{id}")
    public int delete(@PathVariable Integer id) {
        return supplierService.delete(id);
    }
}
