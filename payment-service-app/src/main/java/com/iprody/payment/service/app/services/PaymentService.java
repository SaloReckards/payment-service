package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.persistence.entity.Payment;
import com.iprody.payment.service.app.persistency.PaymentFilter;
import com.iprody.payment.service.app.persistency.PaymentFilterFactory;
import com.iprody.payment.service.app.persistency.PaymentRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    public List<Payment> search(PaymentFilter filter) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return repository.findAll(spec);
    }

    public Page<Payment> searchPaged(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        return repository.findAll(spec, pageable);
    }
}
