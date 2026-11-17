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
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        // calling third party UPI payment gateway to process the payment
        log.info("Processing UPI payment for paymentId: {}", request.paymentId());
        final String VPA_CODE_FAIL="fail@okaxis";

        String Vpa = request.methodDetails() != null ? request.methodDetails().get("vpa").toString() : null;
        if (Vpa == null || Vpa.equals(VPA_CODE_FAIL)) {
            log.error("VPA is missing or invalid for paymentId: {}", request.paymentId());
            return PaymentProcessorResponse.Failure.builder()
                    .errorCode("VPA_FAILURE")
                    .errorDescription("VPA is missing or invalid")
                    .build();
        }
        String processorRef = "UPI_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        String redirectUrl = "https://upi.example.com/redirect?paymentId=" + request.paymentId() + "&processorRef=" + processorRef;

        return PaymentProcessorResponse.Success.builder()
                .processorReference(processorRef)
                .bankReference(redirectUrl)
                .build();
    }
}
