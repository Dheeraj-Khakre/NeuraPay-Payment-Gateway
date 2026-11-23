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
public class WallatPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        // calling third party UPI payment gateway to process the payment
        log.info("Processing Wallet payment for paymentId: {}", request.paymentId());
        final String WALLET_CODE_FAIL="fail@pytum";

        String wallet = request.methodDetails() != null ? request.methodDetails().get("wallet").toString() : null;
        if (wallet == null || wallet.equals(WALLET_CODE_FAIL)) {
            log.error("WALLET_CODE is missing or invalid for paymentId: {}", request.paymentId());
            return PaymentProcessorResponse.Failure.builder()
                    .errorCode("WALLET_CODE_FAILURE")
                    .errorDescription("WALLET_CODE is missing or invalid")
                    .build();
        }
        String processorRef = "WLT_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        String redirectUrl = "https://pytum.example.com/redirect?paymentId=" + request.paymentId() + "&processorRef=" + processorRef;

        return PaymentProcessorResponse.Pending.builder()
                .processorReference(processorRef)
                .build();
    }
}
