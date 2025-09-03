package com.iprody.payment.service.app.async;

import com.iprody.payment.service.app.persistency.PaymentRepository;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import org.springframework.stereotype.Component;


@Component
public class XPaymentResponseHandle implements MessageHandler<XPaymentAdapterResponseMessage> {
    private final PaymentRepository paymentRepository;

    public XPaymentResponseHandle(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        paymentRepository.findById(message.getPaymentGuid())
                .ifPresent(payment -> {
                    PaymentStatus newStatus = switch (message.getStatus()) {
                        case SUCCEEDED -> PaymentStatus.APPROVED;
                        case CANCELED -> PaymentStatus.DECLINED;
                        case PROCESSING -> PaymentStatus.PENDING;
                    };

                    payment.setStatus(newStatus);
                    payment.setTransactionRefId(message.getTransactionRefId());
                    payment.setUpdatedAt(message.getOccurredAt());

                    paymentRepository.save(payment);
                });
    }
}
