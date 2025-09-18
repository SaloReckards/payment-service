package com.iprody.xpaymentadapterapp.async;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

//Сообщение-запрос для платёжной системы X Payment. Используется для передачи информации о платеже
public class XPaymentAdapterRequestMessage implements Message {
    private UUID paymentGuid;                       // Уникальный идентификатор платежа
    private BigDecimal amount;                      // Сумма платежа
    private String currency;                        // Валюта платежа в формате ISO 4217 ("EUR", "USD")
    private OffsetDateTime occurredAt;              // Момент времени, когда событие произошло

    @Override
    public UUID getMessageId() {
        return paymentGuid;
    }

    public UUID getPaymentGuid() {
        return paymentGuid;
    }

    public void setPaymentGuid(UUID paymentGuid) {
        this.paymentGuid = paymentGuid;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(OffsetDateTime occurredAt) {
        this.occurredAt = occurredAt;
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

}
