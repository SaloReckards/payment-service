package com.iprody.payment.service.app.async;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

@Service
public class InMemoryXPaymentAdapterMessageBroker implements AsyncSender<XPaymentAdapterRequestMessage> {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final AsyncListener<XPaymentAdapterResponseMessage> resultListener;

    @Autowired
    public InMemoryXPaymentAdapterMessageBroker(AsyncListener<XPaymentAdapterResponseMessage> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void send(XPaymentAdapterRequestMessage request) {
        UUID uuid = UUID.randomUUID();
        XPaymentAdapterResponseMessage initial = emit(request, uuid, XPaymentAdapterStatus.PROCESSING);
        resultListener.onMessage(initial);

        scheduler.schedule(() -> {
            boolean even = request.getAmount().remainder(new java.math.BigDecimal(2))
                    .compareTo(java.math.BigDecimal.ZERO) == 0;

            XPaymentAdapterStatus finalStatus = even
                    ? XPaymentAdapterStatus.SUCCEEDED
                    : XPaymentAdapterStatus.CANCELED;

            XPaymentAdapterResponseMessage response = emit(request, uuid, finalStatus);
            resultListener.onMessage(response);
        }, 30, TimeUnit.SECONDS);
    }

    private XPaymentAdapterResponseMessage emit(XPaymentAdapterRequestMessage request, UUID txId, XPaymentAdapterStatus status) {
        XPaymentAdapterResponseMessage result = new XPaymentAdapterResponseMessage();
        result.setPaymentGuid(request.getPaymentGuid());
        result.setAmount(request.getAmount());
        result.setCurrency(request.getCurrency());
        result.setTransactionRefId(txId);
        result.setStatus(status);
        result.setOccurredAt(OffsetDateTime.now());
        resultListener.onMessage(result);
        return result;
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}
