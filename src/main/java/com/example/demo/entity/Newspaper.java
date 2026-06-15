package com.example.demo.entity;

import java.math.BigDecimal;

public class Newspaper {
    private Integer id;
    private String name;
    private BigDecimal price;
    private String period;
    private Integer stock;

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}