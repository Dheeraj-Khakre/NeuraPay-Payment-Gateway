package com.dheerxpay.payment.proccessor.strategy;

import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.payment.proccessor.PaymentProcessor;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorRequest;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        // calling third party net banking payment gateway to process the payment
        log.info("Processing net banking payment for paymentId: {}", request.paymentId());
         final String BANK_CODE_FAIL="BANK_CODE_FAIL";

         String BankCode = request.methodDetails() != null ? request.methodDetails().get("bankCode").toString() : null;
        if (BankCode == null || BankCode.equals(BANK_CODE_FAIL)) {
            log.error("Bank code is missing or invalid for paymentId: {}", request.paymentId());
           return PaymentProcessorResponse.Failure.builder()
                   .errorCode("BANK_FAILURE")
                   .errorDescription("Bank code is missing or invalid")
                   .build();
        }
        String processorRef = "NBP_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        String redirectUrl = "https://netbanking.example.com/redirect?paymentId=" + request.paymentId() + "&processorRef=" + processorRef;

        return PaymentProcessorResponse.Success.builder()
                .processorReference(processorRef)
                .bankReference(redirectUrl)
                .build();
    }
}
