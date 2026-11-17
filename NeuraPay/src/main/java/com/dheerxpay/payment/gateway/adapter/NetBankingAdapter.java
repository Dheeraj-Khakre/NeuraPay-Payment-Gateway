package com.dheerxpay.payment.gateway.adapter;

import com.dheerxpay.common.enums.PaymentMethod;
import com.dheerxpay.payment.gateway.PaymentAdapter;
import com.dheerxpay.payment.gateway.dto.PaymentRequest;
import com.dheerxpay.payment.gateway.dto.PaymentResult;
import com.dheerxpay.payment.proccessor.PaymentProcessor;
import com.dheerxpay.payment.proccessor.PaymentProcessorRouter;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorRequest;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor

public class NetBankingAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;
    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        // Implementation for initiating net banking payment
          log.info("Initiating net banking payment for paymentId: {}", paymentRequest.paymentId());
       try {


           PaymentProcessorRequest request = PaymentProcessorRequest.nonCard(
                   paymentRequest.paymentId(),
                   PaymentMethod.NETBANKING,
                   paymentRequest.amount(),
                   paymentRequest.methodDetails()
           );
           PaymentProcessorResponse processorResponse = paymentProcessorRouter.charge(request);


           return switch (processorResponse) {
               case PaymentProcessorResponse.Pending pending ->
                       PaymentResult.Pending.builder()
                               .registerReference(pending.processorReference())
                               .build();
               case PaymentProcessorResponse.Success success ->
                        PaymentResult.Success.builder()
                                .bankReference(success.bankReference())
                                .processorReference(success.processorReference())
                                .build();
               case PaymentProcessorResponse.Failure failure ->
                        PaymentResult.Failure.builder()
                                .ErrorCode(failure.errorCode())
                                .ErrorDescription(failure.errorDescription())
                                .build();
           };
       }catch (Exception e) {
           log.error("Error initiating net banking payment for paymentId: {}. Error: {}", paymentRequest.paymentId(), e.getMessage());
           return new PaymentResult.Failure("NETBANKING_INITIATION_ERROR", "Failed to initiate net banking payment.");
       }
    }

    @Override
    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        return PaymentResult.Success.builder()
                .processorReference("NBP_PROCESSOR")
                .bankReference("https://netbanking.example.com/redirect?paymentId=" + paymentId + "&processorRef=NBP_PROCESSOR")
                .build();
    }
}
