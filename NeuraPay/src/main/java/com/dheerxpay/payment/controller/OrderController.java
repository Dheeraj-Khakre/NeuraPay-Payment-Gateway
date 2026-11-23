package com.dheerxpay.payment.controller;


import com.dheerxpay.common.api.ApiResponse;
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
    private final UUID merchantId = UUID.fromString("352318c7-7479-4b57-ad5c-c365678bd971");// todo:  get merchant id from merchant context

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@RequestBody @Valid OrderCreateRequest request){

        OrderResponse response = orderService.create(merchantId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Order Created Successfully",
                        response
                )
        );

    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable UUID orderId){
        OrderResponse response = orderService.gerById(merchantId,orderId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Order Fetched Successfully",
                        response
                )
        );
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable UUID orderId){
        OrderResponse response = orderService.cancelOrder(merchantId,orderId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Order Cancelled Successfully",
                        response
                )
        );
    }
    @GetMapping("/{orderId}/payments")
    public ResponseEntity<ApiResponse<?>> getListPayment(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Payment List Fetched Successfully",
                        orderService.getListPayment(merchantId, orderId)
                )
        );
    }

    @GetMapping("/merchant")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(){
        List<OrderResponse> orderResponseList = orderService.getListOrders(merchantId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        "Got all list or order ",

                        orderResponseList
                )
        );
     }



}
