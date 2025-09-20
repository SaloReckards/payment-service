package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

public interface XPaymentProviderGateway {
    CreateChargeResponseDto createChargeDto(CreateChargeRequestDto createChargeRequestDto)
            throws RestClientException;

    CreateChargeResponseDto retrieveChargeDto(UUID id) throws RestClientException;
}
