// 获取短码
package com.demo.mapper;

import com.demo.entity.short_link;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface short_linkmapper {
        /**
     * 根据短码查询完整信息
     * 用于跳转时获取长链接
     */
   @Select("SELECT long_url FROM short_link WHERE short_code = #{shortCode}")
    String getLongUrlByShortCode(@Param("shortCode") String shortCode);
}
