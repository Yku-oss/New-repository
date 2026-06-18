//报纸业务实现类，实现报纸增删改查及库存管理等业务逻辑
package com.example.demo.service.impl;

import com.example.demo.entity.InventoryLog;
import com.example.demo.entity.Newspaper;
import com.example.demo.mapper.InventoryLogMapper;
import com.example.demo.mapper.NewspaperMapper;
import com.example.demo.service.NewspaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NewspaperServiceImpl implements NewspaperService {
    
    @Autowired
    private NewspaperMapper newspaperMapper;

    @Autowired
    private InventoryLogMapper inventoryLogMapper;
    
    @Override
    public List<Newspaper> getAll() {
        return newspaperMapper.findAll();
    }
    
    @Override
    public Newspaper getById(Integer id) {
        return newspaperMapper.findById(id);
    }

    @Override
    public List<Newspaper> getByCategoryId(Integer categoryId) {
        return newspaperMapper.findByCategoryId(categoryId);
    }

    @Override
    public List<Newspaper> getRecommended() {
        return newspaperMapper.findRecommended();
    }

    @Override
    public List<Newspaper> getLowStock(Integer threshold) {
        return newspaperMapper.findLowStock(threshold);
    }
    
    @Override
    public int add(Newspaper newspaper) {
        int result = newspaperMapper.insert(newspaper);
        // 记录入库日志
        if (result > 0 && newspaper.getStock() != null && newspaper.getStock() > 0) {
            InventoryLog log = new InventoryLog();
            log.setNewspaperId(newspaper.getId());
            log.setChangeQuantity(newspaper.getStock());
            log.setType("IN");
            log.setRemark("初始化入库");
            inventoryLogMapper.insert(log);
        }
        return result;
    }
    
    @Override
    public int update(Newspaper newspaper) {
        return newspaperMapper.update(newspaper);
    }

    @Override
    public int updateStock(Integer id, Integer quantity) {
        int result = newspaperMapper.updateStock(id, quantity);
        if (result > 0) {
            InventoryLog log = new InventoryLog();
            log.setNewspaperId(id);
            log.setChangeQuantity(Math.abs(quantity));
            log.setType(quantity > 0 ? "IN" : "OUT");
            log.setRemark(quantity > 0 ? "入库" : "出库");
            inventoryLogMapper.insert(log);
        }
        return result;
    }
    
    @Override
    public int delete(Integer id) {
        return newspaperMapper.deleteById(id);
    }
}