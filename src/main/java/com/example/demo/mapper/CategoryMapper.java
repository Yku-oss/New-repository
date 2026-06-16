package com.example.demo.mapper;

import com.example.demo.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
    
    @Select("SELECT * FROM category")
    List<Category> findAll();
    
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category findById(@Param("id") Integer id);
    
    @Insert("INSERT INTO category (name, description) VALUES (#{name}, #{description})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Category category);
    
    @Update("UPDATE category SET name=#{name}, description=#{description} WHERE id=#{id}")
    int update(Category category);
    
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
