package com.example.demo.service;

import com.example.demo.entity.Supplier;
import java.util.List;

public interface SupplierService {
    List<Supplier> getAll();
    Supplier getById(Integer id);
    int add(Supplier supplier);
    int update(Supplier supplier);
    int delete(Integer id);
}
