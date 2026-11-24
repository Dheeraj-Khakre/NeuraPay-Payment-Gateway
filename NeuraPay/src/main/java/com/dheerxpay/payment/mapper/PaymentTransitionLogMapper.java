package com.dheerxpay.payment.mapper;

import com.dheerxpay.operations.dto.PaymentTransitionLogDto;
import com.dheerxpay.payment.entities.PaymentTransitionLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentTransitionLogMapper {

    @Mapping(target = "paymentId", source = "payment.id")
    PaymentTransitionLogDto toDto(PaymentTransitionLog paymentTransitionLog);
}
