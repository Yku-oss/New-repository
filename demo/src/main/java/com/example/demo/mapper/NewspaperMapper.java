//报纸数据访问接口，定义报纸表的数据库操作（CRUD、库存管理等）
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

    @Select("SELECT * FROM newspaper WHERE category_id = #{categoryId}")
    List<Newspaper> findByCategoryId(@Param("categoryId") Integer categoryId);

    @Select("SELECT * FROM newspaper WHERE recommended = true")
    List<Newspaper> findRecommended();

    @Select("SELECT * FROM newspaper WHERE stock < #{threshold}")
    List<Newspaper> findLowStock(@Param("threshold") Integer threshold);

    @Insert("INSERT INTO newspaper (name, price, period, stock, category_id, supplier_id, recommended, description, image_url) " +
            "VALUES (#{name}, #{price}, #{period}, #{stock}, #{categoryId}, #{supplierId}, #{recommended}, #{description}, #{imageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Newspaper newspaper);

    @Update("UPDATE newspaper SET name=#{name}, price=#{price}, period=#{period}, stock=#{stock}, " +
            "category_id=#{categoryId}, supplier_id=#{supplierId}, recommended=#{recommended}, " +
            "description=#{description}, image_url=#{imageUrl} WHERE id=#{id}")
    int update(Newspaper newspaper);

    @Update("UPDATE newspaper SET stock = stock + #{quantity} WHERE id = #{id}")
    int updateStock(@Param("id") Integer id, @Param("quantity") Integer quantity);

    @Delete("DELETE FROM newspaper WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}