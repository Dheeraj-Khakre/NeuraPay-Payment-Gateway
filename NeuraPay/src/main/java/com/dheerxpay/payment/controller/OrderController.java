package com.dheerxpay.payment.controller;


import com.dheerxpay.common.api.ApiResponse;
import com.dheerxpay.merchant.security.MerchantPrincipal;
import com.dheerxpay.payment.dto.request.OrderCreateRequest;
import com.dheerxpay.payment.dto.response.OrderResponse;
import com.dheerxpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestBody @Valid OrderCreateRequest request
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();

        OrderResponse response = orderService.create(principal.merchantId(),request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Order Created Successfully",
                        response
                )
        );

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable UUID orderId
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();
        OrderResponse response = orderService.gerById(principal.merchantId(),orderId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Order Fetched Successfully",
                        response
                )
        );
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable UUID orderId
    ){
        MerchantPrincipal principal = MerchantPrincipal.current();
        OrderResponse response = orderService.cancelOrder(principal.merchantId(),orderId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Order Cancelled Successfully",
                        response
                )
        );
    }
    @GetMapping("/{orderId}/payments")
    public ResponseEntity<ApiResponse<?>> getListPayment(
            @PathVariable UUID orderId
    ) {
        MerchantPrincipal principal = MerchantPrincipal.current();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Payment List Fetched Successfully",
                        orderService.getListPayment(principal.merchantId(), orderId)
                )
        );
    }

    @GetMapping("/merchant")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        MerchantPrincipal principal = MerchantPrincipal.current();
        List<OrderResponse> orderResponseList = orderService.getListOrders(principal.merchantId());
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Got all list or order ",

                        orderResponseList
                )
        );
     }



}
