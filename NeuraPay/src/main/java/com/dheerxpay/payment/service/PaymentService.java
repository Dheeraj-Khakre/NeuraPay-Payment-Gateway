package com.dheerxpay.payment.service;


import com.dheerxpay.payment.dto.request.PaymentInitRequest;
import com.dheerxpay.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capturePayment(UUID merchantId, UUID paymentId);

    void resolveAuthorization(UUID paymentId, boolean approve, String bankRef , String errorCode, String errorDescription);

    List<PaymentResponse> getAllPayments(UUID merchantId);
}
