package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.persistance.entity.Payment;
import com.iprody.payment.service.app.persistency.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private PaymentRepository repository;

    public PaymentController(PaymentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{guid}")
    public Payment getPayments(@PathVariable UUID guid) {
        return repository.findById(guid).orElse(null);
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    @PostMapping
    public Payment createPayment(@RequestBody Payment payment) {
        payment.setGuid(UUID.randomUUID());
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());
        return repository.save(payment);
    }
}
