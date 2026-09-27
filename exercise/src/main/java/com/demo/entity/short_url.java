package com.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName; // 引入表名注释
import com.baomidou.mybatisplus.annotation.TableId; // 引入主键id
import com.baomidou.mybatisplus.annotation.IdType; // 引入主键id的类型
import java.time.LocalDateTime;

@TableName("t_short_url")// 声明这是一个表的方法
public class short_url {
    
    @TableId(type = IdType.AUTO)//表明 主键id和 主键类型
    // 引入数据
    private Long id;
    private String shortCode;
    private String longUrl;
    private String usrID;
    private Long clickCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
}
