package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.entity.Payment;
import com.iprody.payment.service.app.persistency.PaymentFilter;
import com.iprody.payment.service.app.persistency.PaymentFilterFactory;
import com.iprody.payment.service.app.persistency.PaymentRepository;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final PaymentMapper mapper;

    public PaymentServiceImpl(PaymentRepository repository, PaymentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = mapper.toEntity(dto);
        entity.setGuid(null);
        entity.setCreatedAt(OffsetDateTime.now());
        entity.setUpdatedAt(OffsetDateTime.now());
        Payment saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public PaymentDto get(UUID id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow(()
                -> new EntityNotFoundException("Платеж не найден", "get", id));
    }

    @Override
    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        Specification<Payment> spec = PaymentFilterFactory.fromFilter(filter);
        Page<Payment> page = repository.findAll(spec, pageable);
        return page.map(mapper::toDto);
    }

    @Override
    public PaymentDto update(UUID id, PaymentDto dto) {
        Payment existing = repository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Платеж не найден", "update", id));

        Payment updated = mapper.toEntity(dto);
        updated.setGuid(id);
        Payment saved = repository.save(updated);
        return mapper.toDto(saved);
    }

    @Override
    public PaymentDto updateStatus(UUID id, PaymentStatus status) {
        Payment existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "updateStatus", id));
        existing.setStatus(status);
        Payment saved = repository.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public PaymentDto updateNote(UUID id, String note) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Платеж не найден", "updateNote", id));
        payment.setNote(note);
        Payment saved = repository.save(payment);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Платеж не найден", "delete", id);
        }
        repository.deleteById(id);
    }
}
