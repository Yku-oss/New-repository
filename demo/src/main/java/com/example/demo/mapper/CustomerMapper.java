//客户数据访问接口，定义客户表的数据库操作（CRUD）
package com.example.demo.mapper;

import com.example.demo.entity.Customer;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CustomerMapper {
    // Mybatis中的注释方法，使用注解直接编写SQL语句，简化了XML配置文件的编写
    // 隔开数据库与Java代码，使得操作数据库中的数据更加方便
    @Select("SELECT * FROM customer")
    List<Customer> findAll();
    
    // 根据数据的Id查询所需的客户数据信息，并将查询结果映射为Customer对象返回
    // Customer对象就是数据库entity包中的一个类，其中包含了客户表的字段属性
    // 相当于Mapper从数据库中查询到的数据，然后返回给Service层进行业务处理
    // @Param注解用于指定SQL语句中的参数名称，使得SQL语句中的参数能够正确地绑定到方法参数上
    // findById的命名规范符合Mybatis的约定，表示根据Id查询数据
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer findById(@Param("id") Integer id);

    // 根据客户的邮箱查询客户信息
    // #{}占位符用于将方法参数绑定到SQL语句中，
    // #{email}表示将方法参数email的值绑定到SQL语句中的#{email}位置
    @Select("SELECT * FROM customer WHERE email = #{email}")
    Customer findByEmail(@Param("email") String email);
    
    // 插入一条新的客户数据到数据库中，使用@Options注解指定使用自动生成的主键，
    // 并将生成的主键值设置到Customer对象的id属性上，方便后续操作
    @Insert("INSERT INTO customer (name, phone, address, email, password, role) " +
            "VALUES (#{name}, #{phone}, #{address}, #{email}, #{password}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Customer customer);
    
    @Update("UPDATE customer SET name=#{name}, phone=#{phone}, address=#{address}, " +
            "email=#{email} WHERE id=#{id}")
    int update(Customer customer);
    
    @Delete("DELETE FROM customer WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}