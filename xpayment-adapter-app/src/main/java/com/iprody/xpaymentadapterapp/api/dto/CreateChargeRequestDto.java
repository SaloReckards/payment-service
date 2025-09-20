package com.iprody.xpaymentadapterapp.api.dto;

import java.math.BigDecimal;

public class CreateChargeRequestDto {
    private BigDecimal amount;
    private String currency;
    private String order;

    public CreateChargeRequestDto(BigDecimal amount, String currency, String order) {
        this.amount = amount;
        this.currency = currency;
        this.order = order;
    }

    public CreateChargeRequestDto() {
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
