package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.exception.EntityNotFoundException;
import com.iprody.payment.service.app.exception.ErrorDto;
import com.iprody.payment.service.app.persistency.PaymentFilter;
import com.iprody.payment.service.app.service.PaymentServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceImpl paymentServiceImpl;

    public PaymentController(PaymentServiceImpl paymentServiceImpl) {
        this.paymentServiceImpl = paymentServiceImpl;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('admin')")
    public PaymentDto create(@RequestBody PaymentDto paymentDto) {
        log.info("Create payment: {}", paymentDto);
        PaymentDto created = paymentServiceImpl.create(paymentDto);
        log.debug("Payment created: {}", created);
        return created;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto get(@PathVariable UUID id) {
        log.info("Get payment by id: {}", id);
        PaymentDto dtoId = paymentServiceImpl.get(id);
        log.debug("Sending response PaymentDto: {}", dtoId);
        return dtoId;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('admin', 'reader')")
    @ResponseStatus(HttpStatus.OK)
    public Page<PaymentDto> search(PaymentFilter filter, Pageable pageable) {
        log.info("Search payment by filter: {}", filter);
        Page<PaymentDto> dtoSearch = paymentServiceImpl.search(filter, pageable);
        log.debug("Sending response PaymentDto: {}", dtoSearch);
        return dtoSearch;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto update(@PathVariable UUID id, @RequestBody PaymentDto paymentDto) {
        log.info("Update payment: {}", paymentDto);
        PaymentDto updateDto = paymentServiceImpl.update(id, paymentDto);
        log.debug("Payment updated: {}", updateDto);
        return updateDto;
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateStatus(@PathVariable UUID id, @RequestBody @Valid PaymentStatusUpdateDto dto) {
        log.info("Update status payment: {}", dto);
        PaymentDto updateStatus = paymentServiceImpl.updateStatus(id, dto.getStatus());
        log.debug("Payment status updated: {}", updateStatus);
        return updateStatus;
    }

    @PatchMapping("/{id}/note")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.OK)
    public PaymentDto updateNote(@PathVariable UUID id, @RequestBody @Valid PaymentNoteUpdateDto dto) {
        log.info("Update note payment: {}", dto);
        PaymentDto updateNote = paymentServiceImpl.updateNote(id, dto.getNote());
        log.debug("Payment note updated: {}", updateNote);
        return updateNote;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('admin')")
    public void delete(@PathVariable UUID id) {
        log.info("Delete payment: {}", id);
        paymentServiceImpl.delete(id);
        log.debug("Payment deleted: {}", id);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFound(EntityNotFoundException ex) {
        return new ErrorDto(HttpStatus.NOT_FOUND.value(), ex.getEntityid(), ex.getOperation(), ex.getMessage());
    }
}
