package com.dheerxpay.payment.gateway.adapter;

import com.dheerxpay.common.enums.PaymentMethod;
import com.dheerxpay.payment.gateway.PaymentAdapter;
import com.dheerxpay.payment.gateway.dto.PaymentRequest;
import com.dheerxpay.payment.gateway.dto.PaymentResult;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import com.dheerxpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardAdapter implements PaymentAdapter {
    private final VaultService vaultService;
    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        String token  = paymentRequest.methodDetails().get("token").toString();
        if(token==null){
            throw new RuntimeException("token  is"+ null);
        }
        PaymentProcessorResponse response = vaultService.charge(paymentRequest.paymentId(), token, paymentRequest.amount(), paymentRequest.methodDetails());

        return switch (response){
            case PaymentProcessorResponse.Success success-> PaymentResult.Success.builder().processorReference(success.processorReference()
            ).bankReference(success.bankReference()).build();
            case PaymentProcessorResponse.Failure failure ->PaymentResult.Failure.builder().ErrorCode(failure.errorCode()).ErrorDescription(failure.errorDescription()).build();
            case PaymentProcessorResponse.Pending pending -> PaymentResult.Pending.builder().registerReference(pending.processorReference())
                    .build();
        };
    }

    @Override
    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        return PaymentResult.Success.builder()
                .processorReference("CARD_PROCESSOR")
                .bankReference("https://CARD.example.com/redirect?paymentId=" + paymentId + "&processorRef=CARD_PROCESSOR")
                .build();
    }
}
