package com.dheerxpay.payment.gateway.dto;

import lombok.Builder;

public  sealed  interface PaymentResult permits PaymentResult.Failure, PaymentResult.Pending, PaymentResult.Success
{
    @Builder
    public record Pending(String registerReference)implements PaymentResult {}
    @Builder
    public record Failure(String ErrorCode,String ErrorDescription) implements PaymentResult{}
    @Builder
    public record Success(String processorReference,String bankReference) implements PaymentResult{}
}
