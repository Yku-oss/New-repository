//付款实体类，记录付款交易信息
package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Integer id;
    private Integer subscriptionId;
    private BigDecimal amount;
    private String paymentMethod;    // 支付宝/微信/银行卡
    private String transactionId;
    private LocalDateTime paymentTime;
    private String status;           // 成功/失败/退款

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSubscriptionId() { return subscriptionId; }
    public void setSubscriptionId(Integer subscriptionId) { this.subscriptionId = subscriptionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
