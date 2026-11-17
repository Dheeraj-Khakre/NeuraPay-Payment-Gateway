package com.dheerxpay.payment.proccessor.dto;

import lombok.Builder;

public sealed interface PaymentProcessorResponse
        permits
        PaymentProcessorResponse.Success,
        PaymentProcessorResponse.Failure,
        PaymentProcessorResponse.Pending {

    @Builder
    record Pending(String processorReference) implements PaymentProcessorResponse {
    }
    @Builder
    record Success(String processorReference, String bankReference) implements PaymentProcessorResponse {
    }

    @Builder
    record Failure(String errorCode, String errorDescription) implements PaymentProcessorResponse {
    }
}