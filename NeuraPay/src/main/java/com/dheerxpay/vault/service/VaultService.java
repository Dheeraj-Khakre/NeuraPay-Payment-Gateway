package com.dheerxpay.vault.service;


import com.dheerxpay.common.entity.Money;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import com.dheerxpay.vault.dto.request.TokenizeRequest;
import com.dheerxpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenizeCard(TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId,String token , Money amount, Map<String,Object> methodDetails);
}
