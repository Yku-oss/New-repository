package com.example.oneday.mapper;

import com.example.oneday.entity.worker;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface workermappings {
    @Select("SELECT * FROM postal_staff")
    List<worker> findAll();

    @Select("SELECT * FROM postal_staff WHERE id = #{id}")
    worker getid(@Param("id") Integer id);
    
    @Select("SELECT * FROM postal_staff WHERE email = #{email}")
    worker getEmail(@Param("email") String email);

    @Select("SELECT * FROM postal_staff WHERE department = #{department}")
    List<worker> findByDepartment(@Param("department") String department);

    @Select("SELECT * FROM postal_staff WHERE status = #{status}")
    List<worker> findByStatus(@Param("status") String status);

    @Insert("INSERT INTO postal_staff (name, phone, email, password, position, department, status) " +
            "VALUES (#{name}, #{phone}, #{email}, #{password}, #{position}, #{department}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(worker worker);

    @Update("UPDATE postal_staff SET name=#{name}, phone=#{phone}, email=#{email}, " +
            "position=#{position}, department=#{department}, status=#{status} WHERE id=#{id}")
    int update(worker worker);

    @Update("UPDATE postal_staff SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM postal_staff WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
