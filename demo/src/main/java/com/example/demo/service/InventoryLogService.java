//库存日志业务接口，定义库存日志相关业务逻辑方法
package com.example.demo.service;

import com.example.demo.entity.InventoryLog;
import java.util.List;

public interface InventoryLogService {
    List<InventoryLog> getAll();
    List<InventoryLog> getByNewspaperId(Integer newspaperId);
    int add(InventoryLog inventoryLog);
}
