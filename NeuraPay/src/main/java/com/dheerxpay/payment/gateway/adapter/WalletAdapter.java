package com.dheerxpay.payment.gateway.adapter;

import com.dheerxpay.common.enums.PaymentMethod;
import com.dheerxpay.payment.gateway.PaymentAdapter;
import com.dheerxpay.payment.gateway.dto.PaymentRequest;
import com.dheerxpay.payment.gateway.dto.PaymentResult;
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
public class WalletAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter paymentProcessorRouter;
    @Override
    public PaymentResult initiatePayment(PaymentRequest paymentRequest) {
        // Implementation for initiating UPI payment
        log.info("Initiating WALLET payment for paymentId: {}", paymentRequest.paymentId());
        try {


            PaymentProcessorRequest request = PaymentProcessorRequest.nonCard(
                    paymentRequest.paymentId(),
                    PaymentMethod.WALLET,
                    paymentRequest.amount(),
                    paymentRequest.methodDetails()
            );
            PaymentProcessorResponse processorResponse = paymentProcessorRouter.charge(request);


            return switch (processorResponse) {
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Success success ->
                        new PaymentResult.Success(success.processorReference(), success.bankReference());
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            };
        } catch (Exception e) {
            log.error("Error initiating WALLET payment for paymentId: {}. Error: {}", paymentRequest.paymentId(), e.getMessage());
            return new PaymentResult.Failure("WALLET_INITIATION_ERROR", "Failed to initiate Wallet payment.");
        }
    }

    @Override
    public PaymentResult capture(PaymentMethod method, UUID paymentId) {
        return PaymentResult.Success.builder()
                .processorReference("WLT_PROCESSOR")
                .bankReference("https://paytm.example.com/redirect?paymentId=" + paymentId + "&processorRef=WLT_PROCESSOR")
                .build();
    }
}
