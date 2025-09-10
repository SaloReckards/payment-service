package com.iprody.payment.service.app.async;

import com.iprody.payment.service.app.persistency.PaymentRepository;
import com.iprody.payment.service.app.persistency.entity.Payment;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class XPaymentResponseHandle implements MessageHandler<XPaymentAdapterResponseMessage> {
    private final PaymentRepository paymentRepository;

    public XPaymentResponseHandle(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void handle(XPaymentAdapterResponseMessage message) {
        Optional<Payment> paymentOpt = paymentRepository.findById(message.getPaymentGuid());
        paymentOpt.ifPresent(payment -> {
            payment.setAmount(message.getAmount());
            payment.setCurrency(message.getCurrency());
            payment.setStatus(
                    switch (message.getStatus()) {
                        case SUCCEEDED -> PaymentStatus.APPROVED;
                        case CANCELED -> PaymentStatus.DECLINED;
                        default -> PaymentStatus.PENDING;
                    }
            );
            paymentRepository.save(payment);
        });
    }
}
