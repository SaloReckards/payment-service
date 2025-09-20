package com.iprody.xpaymentadapterapp.async;

import com.iprody.xpaymentadapterapp.api.XPaymentProviderGatewayImpl;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(RequestMessageHandler.class);
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGatewayImpl xPaymentProviderGateway;

    @Autowired
    public RequestMessageHandler(AsyncSender<XPaymentAdapterResponseMessage> asyncSender,
                                 XPaymentProviderGatewayImpl xPaymentProviderGateway) {
        this.asyncSender = asyncSender;
        this.xPaymentProviderGateway = xPaymentProviderGateway;
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
            log.info("Payment request with paymentGuid - {} is sent for payment processing. Current status - ",
                    chargeResponse.getStatus());

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.SUCCEEDED);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);
        } catch (RestClientException e) {
            log.error("Error in time of sending payment request with paymentGuid - {}",
                    message.getPaymentGuid(), e);

            XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
            responseMessage.setPaymentGuid(message.getPaymentGuid());
            responseMessage.setAmount(message.getAmount());
            responseMessage.setCurrency(message.getCurrency());
            responseMessage.setStatus(XPaymentAdapterStatus.CANCELED);
            responseMessage.setTransactionRefId(UUID.randomUUID());
            responseMessage.setOccurredAt(OffsetDateTime.now());

            asyncSender.send(responseMessage);
        }
    }
}
