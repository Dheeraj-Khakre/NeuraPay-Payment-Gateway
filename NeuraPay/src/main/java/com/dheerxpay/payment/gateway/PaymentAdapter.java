package com.dheerxpay.payment.gateway;

import com.dheerxpay.common.enums.PaymentMethod;
import com.dheerxpay.payment.gateway.dto.PaymentRequest;
import com.dheerxpay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    PaymentResult initiatePayment(PaymentRequest paymentRequest);

    PaymentResult capture(PaymentMethod method, UUID paymentId);
}
