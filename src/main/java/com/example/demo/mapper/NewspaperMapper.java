package com.example.demo.mapper;

import com.example.demo.entity.Newspaper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewspaperMapper {
    
    @Select("SELECT * FROM newspaper")
    List<Newspaper> findAll();
    
    @Select("SELECT * FROM newspaper WHERE id = #{id}")
    Newspaper findById(@Param("id") Integer id);

    @Insert("INSERT INTO newspaper (name, price, period, stock) VALUES (#{name}, #{price}, #{period}, #{stock})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Newspaper newspaper);

    @Update("UPDATE newspaper SET name=#{name}, price=#{price}, period=#{period}, stock=#{stock} WHERE id=#{id}")
    int update(Newspaper newspaper);

    @Delete("DELETE FROM newspaper WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}