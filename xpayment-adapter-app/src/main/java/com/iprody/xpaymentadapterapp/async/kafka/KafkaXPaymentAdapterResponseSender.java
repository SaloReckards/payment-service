package com.iprody.xpaymentadapterapp.async.kafka;

import com.iprody.xpaymentadapterapp.async.AsyncSender;
import com.iprody.xpaymentadapterapp.async.XPaymentAdapterResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaXPaymentAdapterResponseSender implements AsyncSender<XPaymentAdapterResponseMessage> {
    private static final Logger log =
            LoggerFactory.getLogger(KafkaXPaymentAdapterResponseSender.class);
    private final KafkaTemplate<String, XPaymentAdapterResponseMessage> template;
    private final String topic;

    public KafkaXPaymentAdapterResponseSender
            (KafkaTemplate<String, XPaymentAdapterResponseMessage> template,
             @Value("${app.kafka.topics.xpayment-adapter.response:xpayment-adapter.responses}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    @Override
    public void send(XPaymentAdapterResponseMessage message) {
        String key = message.getPaymentGuid().toString();           //фиксируем партиционирование по платежу
        log.info("Sending XPayment Adapter response: guid={}, amount={}, currency={} -> topic={}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency(), topic);
        template.send(topic, key, message);
    }
}
