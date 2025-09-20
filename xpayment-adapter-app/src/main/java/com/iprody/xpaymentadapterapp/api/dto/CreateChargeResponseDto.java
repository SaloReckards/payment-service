package com.iprody.xpaymentadapterapp.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateChargeResponseDto {
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String order;

    public CreateChargeResponseDto() {
    }

    public CreateChargeResponseDto(UUID id, BigDecimal amount, String currency, String status, String order) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
