package com.iprody.xpaymentadapterapp.checkstate;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PaymentStateCheckRegistrarImpl implements PaymentStateCheckRegistrar {
    Logger log = LoggerFactory.getLogger(PaymentStateCheckRegistrarImpl.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;
    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckRegistrarImpl(RabbitTemplate rabbitTemplate,
                                          @Value("${app.rabbitmq.exchange-name}") String exchangeName,
                                          @Value("${app.rabbitmq.queue-name}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
    }

    @Override
    public void register(UUID chargeGuid, UUID paymentGuid, BigDecimal amount, String currency) {
        log.info("Registering payment for status tracking: chargeGuid={}, paymentGuid={}", chargeGuid, paymentGuid);
        PaymentCheckStateMessage message = new PaymentCheckStateMessage(chargeGuid, paymentGuid, amount, currency);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message,
                m -> {
                    m.getMessageProperties().setHeader("x-delay", intervalMs);
                    m.getMessageProperties().setHeader("x-retry-count", 1);
                    return m;
                }
        );
        log.info("Successfully sent to RabbitMQ: paymentGuid={}, exchange={}, routingKey={}",
                paymentGuid, exchangeName, routingKey);
    }
}
