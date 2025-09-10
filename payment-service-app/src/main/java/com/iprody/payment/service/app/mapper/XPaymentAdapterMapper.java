package com.iprody.payment.service.app.mapper;

import com.iprody.payment.service.app.async.XPaymentAdapterRequestMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.iprody.payment.service.app.persistency.entity.Payment;

@Mapper(componentModel = "spring")
public interface XPaymentAdapterMapper {
    @Mapping(source = "guid", target = "paymentGuid")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "updatedAt", target = "occurredAt")
    XPaymentAdapterRequestMessage toXPaymentAdapterRequestMessage(Payment payment);
}
