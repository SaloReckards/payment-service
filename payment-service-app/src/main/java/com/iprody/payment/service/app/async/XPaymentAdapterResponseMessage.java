package com.iprody.payment.service.app.async;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

// Сообщение-ответ от платёжной системы X Payment
public class XPaymentAdapterResponseMessage implements Message {
    private UUID messageGuid;                           // Уникальный идентификатор сообщения
    private UUID paymentGuid;                           // Уникальный идентификатор платежа
    private BigDecimal amount;                          // Сумма платежа
    private String currency;                            // Валюта платежа в формате ISO 4217 ("EUR", "USD")
    private UUID transactionRefId;                      // Уникальный идентификатор транзакции в платёжной системе
    private XPaymentAdapterStatus status;               // Статус платежа
    private OffsetDateTime occurredAt;                  // Момент времени, когда событие произошло

    @Override
    public UUID getMessageId() {
        return messageGuid;
    }

    public void setMessageGuid(UUID messageGuid) {
        this.messageGuid = messageGuid;
    }

    public UUID getPaymentGuid() {
        return paymentGuid;
    }

    public void setPaymentGuid(UUID paymentGuid) {
        this.paymentGuid = paymentGuid;
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

    public UUID getTransactionRefId() {
        return transactionRefId;
    }

    public void setTransactionRefId(UUID transactionRefId) {
        this.transactionRefId = transactionRefId;
    }

    public XPaymentAdapterStatus getStatus() {
        return status;
    }

    public void setStatus(XPaymentAdapterStatus status) {
        this.status = status;
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(OffsetDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }
}
