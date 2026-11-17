package com.dheerxpay.payment.proccessor.strategy;

import com.dheerxpay.common.entity.Money;
import com.dheerxpay.common.enums.PaymentMethod;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorRequest;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class NetBankingPaymentProcessorTest {

    private final NetBankingPaymentProcessor processor = new NetBankingPaymentProcessor();

    @Test
    void defaultsToPendingWhenNoMockOutcomeIsProvided() {
        PaymentProcessorRequest request = request(null);
        PaymentProcessorResponse response = processor.charge(request);

        PaymentProcessorResponse.Pending pending =
                assertInstanceOf(PaymentProcessorResponse.Pending.class, response);
        assertEquals("mock-netbanking-" + request.processingId(), pending.processorReference());
    }

    @Test
    void returnsSuccessForSuccessfulMockOutcome() {
        PaymentProcessorRequest request = request(Map.of("mockOutcome", "success"));

        PaymentProcessorResponse.Success success = assertInstanceOf(
                PaymentProcessorResponse.Success.class,
                processor.charge(request)
        );
        assertEquals("mock-netbanking-" + request.processingId(), success.processorReference());
        assertEquals("mock-bank-" + request.paymentId(), success.bankReference());
    }

    @Test
    void returnsFailureForFailedMockOutcome() {
        PaymentProcessorResponse response = processor.charge(request(Map.of("mockOutcome", "FAILURE")));

        PaymentProcessorResponse.Failure failure =
                assertInstanceOf(PaymentProcessorResponse.Failure.class, response);
        assertEquals("MOCK_BANK_DECLINED", failure.errorCode());
    }

    @Test
    void rejectsUnknownMockOutcome() {
        PaymentProcessorResponse response = processor.charge(request(Map.of("mockOutcome", "UNKNOWN")));

        PaymentProcessorResponse.Failure failure =
                assertInstanceOf(PaymentProcessorResponse.Failure.class, response);
        assertEquals("INVALID_MOCK_OUTCOME", failure.errorCode());
    }

    private PaymentProcessorRequest request(Map<String, Object> methodDetails) {
        return PaymentProcessorRequest.nonCard(
                UUID.randomUUID(),
                PaymentMethod.NETBANKING,
                Money.inr(100),
                methodDetails
        );
    }
}
