package com.example.demo.entity;

import java.time.LocalDateTime;

public class InventoryLog {
    private Integer id;
    private Integer newspaperId;
    private Integer changeQuantity;
    private String type;        // IN 入库 / OUT 出库
    private String remark;
    private LocalDateTime createTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getNewspaperId() { return newspaperId; }
    public void setNewspaperId(Integer newspaperId) { this.newspaperId = newspaperId; }
    public Integer getChangeQuantity() { return changeQuantity; }
    public void setChangeQuantity(Integer changeQuantity) { this.changeQuantity = changeQuantity; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
