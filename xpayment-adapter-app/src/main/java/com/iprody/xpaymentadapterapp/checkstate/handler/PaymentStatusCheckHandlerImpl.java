package com.iprody.xpaymentadapterapp.checkstate.handler;

import com.iprody.xpaymentadapterapp.api.XPaymentProviderGatewayImpl;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import com.iprody.xpaymentadapterapp.async.AsyncSender;
import com.iprody.xpaymentadapterapp.async.XPaymentAdapterResponseMessage;
import com.iprody.xpaymentadapterapp.async.XPaymentAdapterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class PaymentStatusCheckHandlerImpl implements PaymentStatusCheckHandler {
    private static final Logger log = LoggerFactory.getLogger(PaymentStatusCheckHandlerImpl.class);
    private final XPaymentProviderGatewayImpl xPaymentProviderGateway;
    private final AsyncSender<XPaymentAdapterResponseMessage> asyncSender;

    public PaymentStatusCheckHandlerImpl(XPaymentProviderGatewayImpl xPaymentProviderGateway,
                                         AsyncSender<XPaymentAdapterResponseMessage> asyncSender) {
        this.xPaymentProviderGateway = xPaymentProviderGateway;
        this.asyncSender = asyncSender;
    }

    @Override
    public boolean handle(UUID chargeGuid) {
        try {
            CreateChargeResponseDto chargeResponseDto = xPaymentProviderGateway.retrieveChargeDto(chargeGuid);
            String status = chargeResponseDto.getStatus().name();

            if ("SUCCEEDED".equals(status) || "CANCELED".equals(status)) {
                XPaymentAdapterResponseMessage responseMessage = new XPaymentAdapterResponseMessage();
                responseMessage.setPaymentGuid(UUID.fromString(chargeResponseDto.getOrder()));
                responseMessage.setAmount(chargeResponseDto.getAmount());
                responseMessage.setCurrency(chargeResponseDto.getCurrency());
                responseMessage.setTransactionRefId(chargeResponseDto.getId());
                responseMessage.setOccurredAt(OffsetDateTime.now());
                responseMessage.setStatus(XPaymentAdapterStatus.valueOf(status));
                asyncSender.send(responseMessage);
                log.info("Payment status for chargeGuid{}", chargeGuid);
                return true;
            }

            return false;

        } catch (Exception e) {
            log.info("Error checking payment status for chargeGuid{}", chargeGuid, e);
            return false;
        }
    }
}
