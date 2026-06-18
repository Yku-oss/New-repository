//供应商管理控制器，处理供应商相关的HTTP请求
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

    // RequestBody注解表示从HTTP请求的请求体中获取数据，并将其转换为Supplier对象
    // 就是相当于当前端发送一个HTTP POST请求后，PathVariable注释就会从请求的URL路径出提取id
    // 然后ResquestBody注释就会这个请求中获取并转换为用户所需要的Supplier对象
    // 最后这个对象就会被传输给Service层进行处理
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
