package com.iprody.payment.service.app.controller;

import com.iprody.payment.service.app.model.Payment;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final Map<Long, Payment> payments = new HashMap<>();

    public PaymentController() {
        payments.put(1L, new Payment(1L, 100));
        payments.put(2L, new Payment(2L, 200));
        payments.put(3L, new Payment(3L, 300));
        payments.put(4L, new Payment(4L, 400));
        payments.put(5L, new Payment(5L, 500));
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }
    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable long id) {
        return payments.get(id);
    }
}
