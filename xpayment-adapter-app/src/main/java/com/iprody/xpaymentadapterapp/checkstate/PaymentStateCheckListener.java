package com.iprody.xpaymentadapterapp.checkstate;

import com.iprody.xpaymentadapterapp.checkstate.handler.PaymentStatusCheckHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PaymentStateCheckListener {
    private final static Logger log = LoggerFactory.getLogger(PaymentStateCheckListener.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String routingKey;
    private final String dlxExchangeName;
    private final String dlxRoutingKey;
    private final PaymentStatusCheckHandlerImpl paymentStatusCheckHandlerImpl;

    @Value("${app.rabbitmq.max-retries:60}")
    private int maxRetries;

    @Value("${app.rabbitmq.interval-ms:60000}")
    private long intervalMs;

    @Autowired
    public PaymentStateCheckListener(RabbitTemplate rabbitTemplate,
                                     @Value("${app.rabbitmq.exchange-name}") String exchangeName,
                                     @Value("${app.rabbitmq.queue-name}") String routingKey,
                                     @Value("${app.rabbitmq.dlx-exchange-name}") String dlxExchangeName,
                                     @Value("${app.rabbitmq.dlx-routing-key}") String dlxRoutingKey,
                                     PaymentStatusCheckHandlerImpl paymentStatusCheckHandlerImpl) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.dlxExchangeName = dlxExchangeName;
        this.dlxRoutingKey = dlxRoutingKey;
        this.paymentStatusCheckHandlerImpl = paymentStatusCheckHandlerImpl;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue-name}")
    public void handle(PaymentCheckStateMessage message, Message raw) {
        log.info("RabbitMQ message received for payment: {}", message.getPaymentGuid());
        MessageProperties props = raw.getMessageProperties();
        int retryCount = (int) props.getHeaders().getOrDefault("x-retry-count", 0);

        log.info("Checking status for charge: {} (attempt {})", message.getChargeGuid(), retryCount);

        boolean paid = paymentStatusCheckHandlerImpl.handle(message.getChargeGuid());
        if (paid) {
            log.info("Final status reached for payment: {}", message.getPaymentGuid());
            return;
        }
        if (retryCount < maxRetries) {
            // Планируем следующую проверку
            PaymentCheckStateMessage newMessage = new PaymentCheckStateMessage(message.getChargeGuid(),
                    message.getPaymentGuid(), message.getAmount(), message.getCurrency()
            );
            rabbitTemplate.convertAndSend(exchangeName, routingKey, newMessage,
                    m -> {
                        m.getMessageProperties().setHeader("x-delay", intervalMs);
                        m.getMessageProperties().setHeader("x-retry-count", retryCount + 1);
                        return m;
                    }
            );
        } else {
            // Исчерпали попытки -- кладём сообщение в DLX
            rabbitTemplate.convertAndSend(dlxExchangeName, dlxRoutingKey, message,
                    m -> {
                        m.getMessageProperties().setHeader("x-retry-count", retryCount);
                        m.getMessageProperties().setHeader("x-final-status", "TIMEOUT");
                        m.getMessageProperties().setHeader("x-original-queue", props.getConsumerQueue());
                        return m;
                    }
            );
        }
    }
}
