package com.demo.entity;

import java.time.LocalDateTime;

public class short_link {
    private Long id;
    private String shortCode;
    private String longUrl;
    private Integer accessCount;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;

    // 无参构造（必须）,如果没有无参构建，那么mybatis就无法映射出结果，就不能填充到set
    public short_link() {}

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }
}
