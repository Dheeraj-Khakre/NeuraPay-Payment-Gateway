package com.dheerxpay.payment.service;

import com.dheerxpay.operations.dto.PaymentTransitionLogDto;

import java.util.List;
import java.util.UUID;

public interface PaymentTransitionLogService {
    List<PaymentTransitionLogDto> getAll(UUID merchantId);

    List<PaymentTransitionLogDto> getByPaymentId(UUID merchantId, UUID paymentId);

    PaymentTransitionLogDto getById(UUID merchantId, UUID transactionId);
}
