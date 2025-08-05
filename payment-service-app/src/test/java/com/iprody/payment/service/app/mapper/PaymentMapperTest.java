package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.persistency.entity.Payment;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMapperTest {
    private final PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    void shouldMapEntityToDto() {
        // given
        final UUID guid = UUID.randomUUID();
        final Payment payment = new Payment();
        payment.setGuid(guid);
        payment.setAmount(new BigDecimal("123.45"));
        payment.setCurrency("USD");
        payment.setInquiryRefId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.APPROVED);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setUpdatedAt(OffsetDateTime.now());

        // when
        PaymentDto dto = mapper.toDto(payment);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getGuid()).isEqualTo(guid);
        assertThat(dto.getAmount()).isEqualTo(payment.getAmount());
        assertThat(dto.getCurrency()).isEqualTo(payment.getCurrency());
        assertThat(dto.getInquiryRefId()).isEqualTo(payment.getInquiryRefId());
        assertThat(dto.getStatus()).isEqualTo(payment.getStatus());
        assertThat(dto.getCreatedAt()).isEqualTo(payment.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(payment.getUpdatedAt());
    }

    @Test
    void shouldMapDtoToEntity() {
        // given
        final UUID guid = UUID.randomUUID();
        final PaymentDto dto = new PaymentDto();
        dto.setGuid(guid);
        dto.setAmount(new BigDecimal("999.99"));
        dto.setCurrency("EUR");
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());

        // when
        Payment entity = mapper.toEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getGuid()).isEqualTo(guid);
        assertThat(entity.getAmount()).isEqualTo(dto.getAmount());
        assertThat(entity.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(entity.getInquiryRefId()).isEqualTo(dto.getInquiryRefId());
        assertThat(entity.getStatus()).isEqualTo(dto.getStatus());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.getUpdatedAt());
    }
}
