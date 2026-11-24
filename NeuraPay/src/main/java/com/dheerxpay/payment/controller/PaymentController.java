package com.dheerxpay.payment.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.payment.dto.request.PaymentInitRequest;
import com.dheerxpay.payment.dto.response.PaymentResponse;
import com.dheerxpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor

public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @Valid @RequestBody PaymentInitRequest request
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        PaymentResponse paymentResponse = paymentService.initiatePayment(principal.merchantId(), request);

        // Return the response wrapped in ApiResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Payment initiated successfully",
                paymentResponse));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<ApiResponse<PaymentResponse>> capturePayment(
            @PathVariable UUID paymentId
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        PaymentResponse paymentResponse = paymentService.capturePayment(principal.merchantId(), paymentId);

        return ResponseEntity.ok(ApiResponse.success(
                "Payment capture processed successfully",
                paymentResponse));
    }

    @GetMapping("/all-payment")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {
        MerchantPrincipal principal = MerchantPrincipal.current();
        List<PaymentResponse> payments = paymentService.getAllPayments(principal.merchantId());
        return ResponseEntity.ok(ApiResponse.success("All payments retrieved successfully", payments));
    }

}
