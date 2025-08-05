package com.iprody.payment.service.app.services;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.mapper.PaymentMapper;
import com.iprody.payment.service.app.persistency.PaymentRepository;
import com.iprody.payment.service.app.persistency.entity.Payment;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment payment;
    private PaymentDto paymentDto;
    private UUID guid;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @BeforeEach
    void setUp() {
        guid = UUID.randomUUID();
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        payment = new Payment();
        payment.setGuid(guid);
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setAmount(new BigDecimal("100.0"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        paymentDto = new PaymentDto();
        paymentDto.setGuid(payment.getGuid());
        paymentDto.setCurrency(payment.getCurrency());
        paymentDto.setStatus(payment.getStatus());
        paymentDto.setInquiryRefId(payment.getInquiryRefId());
        paymentDto.setAmount(payment.getAmount());
        paymentDto.setCreatedAt(payment.getCreatedAt());
        paymentDto.setUpdatedAt(payment.getUpdatedAt());
    }

    @Test
    void shouldReturnPaymentById() {
        // given
        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.get(guid);

        // then
        assertEquals(guid, result.getGuid());
        assertEquals("USD", result.getCurrency());
        assertEquals(PaymentStatus.APPROVED, result.getStatus());
        assertEquals(BigDecimal.valueOf(100.00), result.getAmount());
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());

        //verify(paymentRepository).findById(guid);  - uncomment if there are no @MockitoSettings
        //verify(paymentMapper).toDto(payment);      - uncomment if there are no @MockitoSettings
    }

    @ParameterizedTest
    @MethodSource("statusProvider")
    void shouldMapDifferentPaymentStatuses(PaymentStatus status) {
        // given
        payment.setStatus(status);
        paymentDto.setStatus(status);

        when(paymentRepository.findById(guid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        // when
        PaymentDto result = paymentService.get(guid);

        // then
        assertEquals(status, result.getStatus());
        verify(paymentRepository).findById(guid);
        verify(paymentMapper).toDto(payment);
    }

    static Stream<PaymentStatus> statusProvider() {
        return Stream.of(
                PaymentStatus.RECEIVED,
                PaymentStatus.PENDING,
                PaymentStatus.APPROVED,
                PaymentStatus.DECLINED,
                PaymentStatus.NOT_SENT
        );
    }
}
