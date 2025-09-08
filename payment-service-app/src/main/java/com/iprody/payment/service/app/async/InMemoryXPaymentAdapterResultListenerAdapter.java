package com.iprody.payment.service.app.async;

import org.springframework.stereotype.Component;

@Component
public class InMemoryXPaymentAdapterResultListenerAdapter implements AsyncListener<XPaymentAdapterResponseMessage> {
    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    public InMemoryXPaymentAdapterResultListenerAdapter(MessageHandler<XPaymentAdapterResponseMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterResponseMessage message) {
        handler.handle(message);
    }
}
