package com.iprody.xpaymentadapterapp.async.kafka;

import com.iprody.xpaymentadapterapp.async.AsyncListener;
import com.iprody.xpaymentadapterapp.async.MessageHandler;
import com.iprody.xpaymentadapterapp.async.XPaymentAdapterRequestMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaXPaymentAdapterRequestListenerAdapter implements AsyncListener<XPaymentAdapterRequestMessage> {
    private static final Logger log =
            LoggerFactory.getLogger(KafkaXPaymentAdapterRequestListenerAdapter.class);
    private final MessageHandler<XPaymentAdapterRequestMessage> handler;

    public KafkaXPaymentAdapterRequestListenerAdapter(MessageHandler<XPaymentAdapterRequestMessage> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(XPaymentAdapterRequestMessage message) {
        handler.handle(message);
    }

    @KafkaListener(topics = "${app.kafka.topics.xpayment-adapter.request}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(XPaymentAdapterRequestMessage message,
                        ConsumerRecord<String, XPaymentAdapterRequestMessage> record, Acknowledgment ack) {
        try {
            log.info("Received XPay adapter response:paymentGuid={}, partitions={}, offset={}",
                    message.getPaymentGuid(), record.partition(), record.offset());
            onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error handling XPayment Adapter request for paymentGuid={}", message.getPaymentGuid(), e);
            throw e;  // отдаём в error handler Spring Kafka
        }
    }
}
