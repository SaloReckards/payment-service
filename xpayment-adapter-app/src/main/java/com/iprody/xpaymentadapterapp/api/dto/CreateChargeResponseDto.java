package com.iprody.xpaymentadapterapp.api.dto;

import com.iprody.xpaymentadapterapp.async.XPaymentAdapterStatus;

import java.math.BigDecimal;
import java.util.UUID;

public class CreateChargeResponseDto {
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private XPaymentAdapterStatus status;
    private String order;

    public CreateChargeResponseDto() {
    }

    public CreateChargeResponseDto(UUID id, BigDecimal amount, String currency, XPaymentAdapterStatus status, String order) {
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

    public XPaymentAdapterStatus getStatus() {
        return status;
    }

    public void setStatus(XPaymentAdapterStatus status) {
        this.status = status;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
