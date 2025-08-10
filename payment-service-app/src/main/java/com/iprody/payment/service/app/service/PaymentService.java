package com.iprody.payment.service.app.service;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistency.PaymentFilter;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PaymentService {
    PaymentDto get(UUID id);

    Page<PaymentDto> search(PaymentFilter filter, Pageable pageable);

    void delete(UUID id);

    PaymentDto create(PaymentDto dto);

    PaymentDto update(UUID id, PaymentDto dto);

    PaymentDto updateNote(UUID id, @NotNull String note);

    PaymentDto updateStatus(UUID id, @NotNull PaymentStatus status);
}
