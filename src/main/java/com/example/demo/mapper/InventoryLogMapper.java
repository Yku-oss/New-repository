package com.example.demo.mapper;

import com.example.demo.entity.InventoryLog;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface InventoryLogMapper {
    
    @Select("SELECT * FROM inventory_log ORDER BY create_time DESC")
    List<InventoryLog> findAll();
    
    @Select("SELECT * FROM inventory_log WHERE newspaper_id = #{newspaperId} ORDER BY create_time DESC")
    List<InventoryLog> findByNewspaperId(@Param("newspaperId") Integer newspaperId);
    
    @Insert("INSERT INTO inventory_log (newspaper_id, change_quantity, type, remark) " +
            "VALUES (#{newspaperId}, #{changeQuantity}, #{type}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InventoryLog inventoryLog);
}
