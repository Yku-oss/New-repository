package com.example.demo.mapper;

import com.example.demo.entity.Supplier;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SupplierMapper {
    
    @Select("SELECT * FROM supplier")
    List<Supplier> findAll();
    
    @Select("SELECT * FROM supplier WHERE id = #{id}")
    Supplier findById(@Param("id") Integer id);
    
    @Insert("INSERT INTO supplier (name, contact_person, phone, address) VALUES (#{name}, #{contactPerson}, #{phone}, #{address})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Supplier supplier);
    
    @Update("UPDATE supplier SET name=#{name}, contact_person=#{contactPerson}, phone=#{phone}, address=#{address} WHERE id=#{id}")
    int update(Supplier supplier);
    
    @Delete("DELETE FROM supplier WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
