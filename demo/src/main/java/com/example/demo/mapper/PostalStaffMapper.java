//邮局工作人员数据访问接口，定义工作人员表的数据库操作（CRUD）
package com.example.demo.mapper;

import com.example.demo.entity.PostalStaff;
import org.apache.ibatis.annotations.*; // 导入Mytabis的注释工具包，apache是开源，ibatis = Mybatis
import java.util.List;

@Mapper
public interface PostalStaffMapper {

    @Select("SELECT * FROM postal_staff")
    List<PostalStaff> findAll(); // 调用这个方法可以返回全部列表

    @Select("SELECT * FROM postal_staff WHERE id = #{id}")
    PostalStaff findById(@Param("id") Integer id);

    @Select("SELECT * FROM postal_staff WHERE email = #{email}")
    PostalStaff findByEmail(@Param("email") String email);

    @Select("SELECT * FROM postal_staff WHERE department = #{department}")
    List<PostalStaff> findByDepartment(@Param("department") String department);

    @Select("SELECT * FROM postal_staff WHERE status = #{status}")
    List<PostalStaff> findByStatus(@Param("status") String status);

    @Insert("INSERT INTO postal_staff (name, phone, email, password, position, department, status) " +
            "VALUES (#{name}, #{phone}, #{email}, #{password}, #{position}, #{department}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PostalStaff postalStaff);

    @Update("UPDATE postal_staff SET name=#{name}, phone=#{phone}, email=#{email}, " +
            "position=#{position}, department=#{department}, status=#{status} WHERE id=#{id}")
    int update(PostalStaff postalStaff);

    @Update("UPDATE postal_staff SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Delete("DELETE FROM postal_staff WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
