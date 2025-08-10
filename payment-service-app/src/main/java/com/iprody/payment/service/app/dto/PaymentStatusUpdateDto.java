package com.iprody.payment.service.app.dto;

import com.iprody.payment.service.app.persistency.entity.PaymentStatus;

import jakarta.validation.constraints.NotNull;


public class PaymentStatusUpdateDto {

    @NotNull
    private PaymentStatus status;

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
