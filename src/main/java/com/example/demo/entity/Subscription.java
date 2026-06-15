package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Subscription {
    private Integer id;
    private Integer customerId;
    private Integer newspaperId;
    private Integer quantity;
    private BigDecimal totalPrice;
    private LocalDateTime orderTime;
    private String status;
    
    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public Integer getNewspaperId() { return newspaperId; }
    public void setNewspaperId(Integer newspaperId) { this.newspaperId = newspaperId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}