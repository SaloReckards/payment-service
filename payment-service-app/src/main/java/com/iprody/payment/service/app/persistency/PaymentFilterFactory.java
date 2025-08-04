package com.iprody.payment.service.app.persistency;

import com.iprody.payment.service.app.persistency.entity.Payment;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class PaymentFilterFactory {
    public static Specification<Payment> fromFilter(PaymentFilter filter) {
        Specification<Payment> spec = Specification.where(null);

        if (StringUtils.hasText(filter.getCurrency())) {
            spec = spec.and(PaymentSpecifications.hasCurrency(filter.getCurrency()));
        }
        if (filter.getMinAmount() != null && filter.getMaxAmount() != null) {
            spec = spec.and(PaymentSpecifications.amountBetween(filter.getMinAmount(), filter.getMaxAmount()));
        }
        if (filter.getCreatedAfter() != null && filter.getCreatedBefore() != null) {
            spec = spec.and(PaymentSpecifications.createdBetween(filter.getCreatedAfter(), filter.getCreatedBefore()));
        }
        if (filter.getStatus() != null) {
            spec = spec.and(PaymentSpecifications.hasStatus(filter.getStatus()));
        }
        return spec;
    }
}
