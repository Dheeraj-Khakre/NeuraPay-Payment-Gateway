package com.dheerxpay.payment.controller;

import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.payment.dto.request.PaymentInitRequest;
import com.dheerxpay.payment.dto.response.PaymentResponse;
import com.dheerxpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor

public class PaymentController {

    private final PaymentService paymentService;
    UUID merchantId = UUID.fromString("352318c7-7479-4b57-ad5c-c365678bd971"); // Replace with your actual merchant ID

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(@Valid @RequestBody PaymentInitRequest request) {
        // Call the payment service to initiate the payment
        PaymentResponse paymentResponse = paymentService.initiatePayment(merchantId ,  request);

        // Return the response wrapped in ApiResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Payment initiated successfully",
                paymentResponse));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<ApiResponse<PaymentResponse>> capturePayment(@PathVariable UUID paymentId) {
        // Call the payment service to initiate the payment
        PaymentResponse paymentResponse = paymentService.capturePayment(merchantId, paymentId);

        // Return the response wrapped in ApiResponse
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Payment initiated successfully",
                paymentResponse));
    }

}

