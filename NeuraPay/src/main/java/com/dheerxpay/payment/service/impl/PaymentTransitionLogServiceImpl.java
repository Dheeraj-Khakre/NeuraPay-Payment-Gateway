package com.dheerxpay.payment.service.impl;

import com.dheerxpay.common.exceptions.ResourceNotFoundException;
import com.dheerxpay.operations.dto.PaymentTransitionLogDto;
import com.dheerxpay.payment.mapper.PaymentTransitionLogMapper;
import com.dheerxpay.payment.repository.PaymentTransitionLogRepo;
import com.dheerxpay.payment.service.PaymentTransitionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentTransitionLogServiceImpl implements PaymentTransitionLogService {

    private final PaymentTransitionLogRepo paymentTransitionLogRepo;
    private final PaymentTransitionLogMapper paymentTransitionLogMapper;

    @Override
    public List<PaymentTransitionLogDto> getAll(UUID merchantId) {
        return paymentTransitionLogRepo.findAllByPayment_MerchantId(merchantId).stream()
                .map(paymentTransitionLogMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentTransitionLogDto> getByPaymentId(UUID merchantId, UUID paymentId) {
        return paymentTransitionLogRepo.findAllByPayment_IdAndPayment_MerchantId(paymentId, merchantId).stream()
                .map(paymentTransitionLogMapper::toDto)
                .toList();
    }

    @Override
    public PaymentTransitionLogDto getById(UUID merchantId, UUID transactionId) {
        return paymentTransitionLogRepo.findByIdAndPayment_MerchantId(transactionId, merchantId)
                .map(paymentTransitionLogMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_TRANSITION_LOG", transactionId));
    }
}
