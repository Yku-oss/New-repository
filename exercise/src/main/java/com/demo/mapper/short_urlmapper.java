package com.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.demo.entity.short_url;
import org.apache.ibatis.annotations.Mapper;

@Mapper// 声明这是一个接口，然后继承实体类short_url,可以调用里面的方法与类名.extends是表继承，BaseMapper是提供Mapper基类
// 提供了一整套CRUD的方法，不用写sql
// 这个接口类继承成了BaseMapper的方法，直接获取mapper的所有sql方法
public interface short_urlmapper extends BaseMapper<short_url> {
}