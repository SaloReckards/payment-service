package com.iprody.xpaymentadapterapp.async;

import com.iprody.xpaymentadapterapp.api.XPaymentProviderGatewayImpl;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import com.iprody.xpaymentadapterapp.checkstate.PaymentStateCheckRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(RequestMessageHandler.class);
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGatewayImpl xPaymentProviderGateway;
    private final PaymentStateCheckRegistrar paymentStateCheckRegistrar;

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> asyncSender,
                                 XPaymentProviderGatewayImpl xPaymentProviderGateway,
                                 PaymentStateCheckRegistrar paymentStateCheckRegistrar) {
        this.asyncSender = asyncSender;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.paymentStateCheckRegistrar = paymentStateCheckRegistrar;
    }

    @Override
    public void handle(XPaymentAdapterRequestMessage message) {
        log.info("Payment request received paymentGuid - {}, amount - {}, currency - {}",
                message.getPaymentGuid(), message.getAmount(), message.getCurrency());
        CreateChargeRequestDto createChargeRequestDto = new CreateChargeRequestDto();
        createChargeRequestDto.setAmount(message.getAmount());
        createChargeRequestDto.setCurrency(message.getCurrency());
        createChargeRequestDto.setOrder(message.getPaymentGuid().toString());

        try {
            CreateChargeResponseDto chargeResponse = xPaymentProviderGateway.createChargeDto(createChargeRequestDto);
            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - {}",
                    message.getPaymentGuid(), chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(chargeResponse.getStatus());
            responseMessage.setTransactionRefId(chargeResponse.getId());
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);

            UUID providerChargeId = chargeResponse.getId();
            UUID paymentGuid = message.getPaymentGuid();
            BigDecimal amount = message.getAmount();
            String currency = message.getCurrency();

            paymentStateCheckRegistrar.register(providerChargeId, paymentGuid, amount, currency);
            log.info("Payment registered for status tracking: providerChargeId={}, paymentGuid - {}",
                    providerChargeId, paymentGuid);
        } catch (
                RestClientException e) {
            log.error("Error in time of sending payment request with paymentGuid - {}",
                    message.getPaymentGuid(), e);

            XPaymentAdapterResponseMessage errorMessage = new XPaymentAdapterResponseMessage();
            errorMessage.setPaymentGuid(message.getPaymentGuid());
            errorMessage.setAmount(message.getAmount());
            errorMessage.setCurrency(message.getCurrency());
            errorMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            errorMessage.setTransactionRefId(UUID.randomUUID());
            errorMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(errorMessage);
        }
    }
}
