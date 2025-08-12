package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistency.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;

    public PaymentController(PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDto create(@RequestBody PaymentDto paymentDto) {
        return paymentServiceImpl.create(paymentDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto get(@PathVariable UUID id) {
        return paymentServiceImpl.get(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        return paymentServiceImpl.search(filter, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        return paymentServiceImpl.update(id, paymentDto);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateStatus(@PathVariable UUID id, @RequestBody @Valid PaymentStatusUpdateDto dto) {
        return paymentServiceImpl.updateStatus(id, dto.getStatus());
    }

    @PatchMapping("/{id}/note")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateNote(@PathVariable UUID id, @RequestBody @Valid PaymentNoteUpdateDto dto) {
        return paymentServiceImpl.updateNote(id, dto.getNote());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        paymentServiceImpl.delete(id);
    }
}
