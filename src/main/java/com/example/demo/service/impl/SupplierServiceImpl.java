//供应商业务实现类，实现供应商增删改查等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.entity.Supplier;
import com.example.demo.mapper.SupplierMapper;
import com.example.demo.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Override
    public List<Supplier> getAll() {
        return supplierMapper.findAll();
    }

    @Override
    public Supplier getById(Integer id) {
        return supplierMapper.findById(id);
    }

    @Override
    public int add(Supplier supplier) {
        return supplierMapper.insert(supplier);
    }

    @Override
    public int update(Supplier supplier) {
        return supplierMapper.update(supplier);
    }

    @Override
    public int delete(Integer id) {
        return supplierMapper.deleteById(id);
    }
}
