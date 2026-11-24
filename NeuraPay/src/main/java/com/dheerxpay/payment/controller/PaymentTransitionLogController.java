package com.dheerxpay.payment.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.operations.dto.PaymentTransitionLogDto;
import com.dheerxpay.payment.service.PaymentTransitionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-transition-logs")
@RequiredArgsConstructor
public class PaymentTransitionLogController {

    private final PaymentTransitionLogService paymentTransitionLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentTransitionLogDto>>> getAll() {
        MerchantPrincipal principal = MerchantPrincipal.current();
        return ResponseEntity.ok(ApiResponse.success(
                "Payment transition logs retrieved successfully",
                paymentTransitionLogService.getAll(principal.merchantId())
        ));
    }

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<ApiResponse<List<PaymentTransitionLogDto>>> getByPaymentId(
            @PathVariable UUID paymentId
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        return ResponseEntity.ok(ApiResponse.success(
                "Payment transition logs retrieved successfully",
                paymentTransitionLogService.getByPaymentId(principal.merchantId(), paymentId)
        ));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentTransitionLogDto>> getById(
            @PathVariable UUID transactionId
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        return ResponseEntity.ok(ApiResponse.success(
                "Payment transition log retrieved successfully",
                paymentTransitionLogService.getById(principal.merchantId(), transactionId)
        ));
    }
}
