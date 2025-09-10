package com.iprody.payment.service.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iprody.payment.service.app.AbstractPostgresIntegrationTest;
import com.iprody.payment.service.app.TestJwtFactory;
import com.iprody.payment.service.app.async.kafka.KafkaXPaymentAdapterRequestSender;
import com.iprody.payment.service.app.async.kafka.KafkaXPaymentAdapterResultListenerAdapter;
import com.iprody.payment.service.app.dto.PaymentDto;
import com.iprody.payment.service.app.dto.PaymentNoteUpdateDto;
import com.iprody.payment.service.app.dto.PaymentStatusUpdateDto;
import com.iprody.payment.service.app.persistency.entity.Payment;
import com.iprody.payment.service.app.persistency.entity.PaymentStatus;
import com.iprody.payment.service.app.persistency.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest extends AbstractPostgresIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KafkaXPaymentAdapterRequestSender kafkaXPaymentAdapterRequestSender;

    @MockBean
    private KafkaXPaymentAdapterResultListenerAdapter kafkaXPaymentAdapterResultListenerAdapter;

    @Test
    void shouldCreatePaymentAndVerifyInDatabase() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.randomUUID());
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setNote("Test note");
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setTransactionRefId(UUID.randomUUID());
        dto.setStatus(PaymentStatus.PENDING);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());

        String json = objectMapper.writeValueAsString(dto);
        String response = mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("sergey", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.guid").exists())
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andReturn()
                .getResponse()
                .getContentAsString();

        PaymentDto created = objectMapper.readValue(response, PaymentDto.class);
        Optional<Payment> saved = paymentRepository.findById(created.getGuid());
        assertThat(saved).isPresent();
        assertThat(saved.get().getCurrency()).isEqualTo("EUR");
        assertThat(saved.get().getAmount()).isEqualByComparingTo("123.45");
    }

    @Test
    void shouldReturnPaymentById() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(get("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("sergey", "reader", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guid").value(existingId.toString()))
                .andExpect(jsonPath("$.currency").value("EUR"))
                .andExpect(jsonPath("$.amount").value(50.00));
    }

    @Test
    void shouldUpdateStatusPayment() throws Exception {
        PaymentStatusUpdateDto dto = new PaymentStatusUpdateDto();
        dto.setStatus(PaymentStatus.PENDING);
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        mockMvc.perform(patch("/payments/" + existingId + "/status")
                        .with(TestJwtFactory.jwtWithRole("sergey", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldUpdateNotePayment() throws Exception {
        PaymentNoteUpdateDto dto = new PaymentNoteUpdateDto();
        dto.setNote("update note in test mode");
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        mockMvc.perform(patch("/payments/" + existingId + "/note")
                        .with(TestJwtFactory.jwtWithRole("sergey", "admin"))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("update note in test mode"));
    }

    @Test
    void shouldDeletePayment() throws Exception {
        UUID existingId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        mockMvc.perform(delete("/payments/" + existingId)
                        .with(TestJwtFactory.jwtWithRole("sergey", "admin"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnOnlyLiquibasePayments() throws Exception {
        mockMvc.perform(get("/payments/search")
                        .with(TestJwtFactory.jwtWithRole("sergey", "reader", "admin"))
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000001')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000002')]").exists())
                .andExpect(jsonPath("$.content[?(@.guid=='00000000-0000-0000-0000-000000000003')]").exists());
    }

    @Test
    void shouldReturn404ForNonexistentPayment() throws Exception {
        UUID nonexistentId = UUID.randomUUID();
        mockMvc.perform(get("/payments/" + nonexistentId)
                        .with(TestJwtFactory.jwtWithRole("sergey", "reader"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode")
                        .value(404))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.operation")
                        .value("Платеж не найден"));
    }

    @Test
    void shouldReturn403ForNonAuthorizedOperation() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.randomUUID());
        dto.setAmount(new BigDecimal("123.45"));
        dto.setCurrency("EUR");
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setTransactionRefId(UUID.randomUUID());
        dto.setNote("update note in test mode");
        dto.setStatus(PaymentStatus.CREATED);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());

        mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("sergey", "reader"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode")
                        .value(403))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn500ForInvalidRequest() throws Exception {
        PaymentDto dto = new PaymentDto();
        dto.setGuid(UUID.randomUUID());
        dto.setAmount(new BigDecimal("12345678"));
        dto.setCurrency("EUR");
        dto.setInquiryRefId(UUID.randomUUID());
        dto.setTransactionRefId(UUID.randomUUID());
        dto.setNote("update note in test mode");
        dto.setStatus(PaymentStatus.CREATED);
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setUpdatedAt(OffsetDateTime.now());
        mockMvc.perform(post("/payments")
                        .with(TestJwtFactory.jwtWithRole("sergey", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode")
                        .value(500))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
