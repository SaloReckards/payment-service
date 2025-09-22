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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RequestMessageHandler implements MessageHandler<XPaymentAdapterRequestMessage> {
    private static final Logger log = LoggerFactory.getLogger(RequestMessageHandler.class);
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;
    private final XPaymentProviderGatewayImpl xPaymentProviderGateway;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

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

            scheduler.schedule(() -> {
                try {
                    CreateChargeResponseDto finalResp = xPaymentProviderGateway.retrieveChargeDto(providerChargeId);
                    XPaymentAdapterResponseMessage finalMessage = new
                            XPaymentAdapterResponseMessage();
                    log.info("Payment request with paymentGuid - {} now have status {}",
                            message.getPaymentGuid(), finalResp.getStatus());
                    finalMessage.setPaymentGuid(message.getPaymentGuid());
                    finalMessage.setAmount(finalResp.getAmount());
                    finalMessage.setCurrency(finalResp.getCurrency());
                    finalMessage.setStatus(finalResp.getStatus());
                    finalMessage.setTransactionRefId(finalResp.getId());
                    finalMessage.setOccurredAt(OffsetDateTime.now());

                    asyncSender.send(finalMessage);
                } catch (Exception e) {
                    log.error("Error retrieving charge for providerChargeId={}", providerChargeId, e);
                }
            }, 65, TimeUnit.SECONDS);
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
