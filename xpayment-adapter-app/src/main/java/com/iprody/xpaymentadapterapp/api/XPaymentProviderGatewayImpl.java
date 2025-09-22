package com.iprody.xpaymentadapterapp.api;

import com.iprody.xpaymentadapterapp.api.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import com.iprody.xpaymentadapterapp.api.mapper.ChargeMapper;
import com.iprody.xpaymentapp.api.client.DefaultApi;
import com.iprody.xpaymentapp.api.model.ChargeResponse;
import com.iprody.xpaymentapp.api.model.CreateChargeRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.UUID;


@Service
public class XPaymentProviderGatewayImpl implements XPaymentProviderGateway {
    private final DefaultApi defaultApi;
    private final ChargeMapper mapper;

    public XPaymentProviderGatewayImpl(DefaultApi defaultApi, ChargeMapper mapper) {
        this.defaultApi = defaultApi;
        this.mapper = mapper;
    }

    @Override
    public CreateChargeResponseDto createChargeDto(CreateChargeRequestDto createChargeRequestDto)
            throws RestClientException {
        CreateChargeRequest request = mapper.toApiRequest(createChargeRequestDto);
        ChargeResponse response = defaultApi.createCharge(request);
        return mapper.toDto(response);
    }

    @Override
    public CreateChargeResponseDto retrieveChargeDto(UUID id) throws RestClientException {
        ChargeResponse response = defaultApi.retrieveCharge(UUID.fromString(id.toString()));
        return mapper.toDto(response);
    }
}
