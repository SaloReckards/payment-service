package com.iprody.xpaymentadapterapp.api.mapper;

import com.iprody.xpaymentadapterapp.api.dto.CreateChargeRequestDto;
import com.iprody.xpaymentadapterapp.api.dto.CreateChargeResponseDto;
import com.iprody.xpaymentapp.api.model.ChargeResponse;
import com.iprody.xpaymentapp.api.model.CreateChargeRequest;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ChargeMapper {
    CreateChargeRequest toApiRequest(CreateChargeRequestDto createChargeRequestDto);

    CreateChargeResponseDto toDto(ChargeResponse chargeResponse);
}
