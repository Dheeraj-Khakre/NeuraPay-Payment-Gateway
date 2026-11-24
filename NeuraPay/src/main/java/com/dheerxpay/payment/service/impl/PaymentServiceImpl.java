package com.dheerxpay.payment.service.impl;

import com.dheerxpay.common.enums.OrderStatus;
import com.dheerxpay.common.enums.PaymentEvent;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.common.exceptions.BusinessRulesViolationException;
import com.dheerxpay.common.exceptions.ResourceNotFoundException;
import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.payment.dto.request.PaymentInitRequest;
import com.dheerxpay.payment.dto.response.PaymentResponse;
import com.dheerxpay.payment.entities.OrderRecord;
import com.dheerxpay.payment.entities.Payment;
import com.dheerxpay.payment.gateway.PaymentGatewayRouter;
import com.dheerxpay.payment.gateway.dto.PaymentRequest;
import com.dheerxpay.payment.gateway.dto.PaymentResult;
import com.dheerxpay.payment.mapper.OrderMapper;
import com.dheerxpay.payment.repository.OrderRepository;
import com.dheerxpay.payment.repository.PaymentRepository;
import com.dheerxpay.payment.service.PaymentService;
import com.dheerxpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final OrderMapper orderMapper;
    private final PaymentTransitionService paymentTransitionService;
    @Override
    @Transactional
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ODER_NOT_FOUND", "Order not found for the given orderId and merchantId"+ request.orderId() + " " + merchantId));

        if(order.getStatus() != null && order.getStatus().name().equals("PAID")) {
            throw new BusinessRulesViolationException("ORDER_NOT_PAYABLE", "Payment has already been made for this order.");
        }
        if(order.getStatus() != null && order.getStatus().name().equals("CANCELLED")) {
            throw new BusinessRulesViolationException("ORDER_CANCELLED", "Payment cannot be initiated for a cancelled order.");
        }
        if(order.getStatus()!= OrderStatus.CREATED && order.getStatus()!= OrderStatus.COMPLETED) {
            throw new BusinessRulesViolationException("ORDER_NOT_PAYABLE", "Payment cannot be initiated for this order status: " + order.getStatus());
        }
        order.setStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);
        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getAmount())
                .idempotencyKey(RandomizerUtil.randomBase64(23))
                .merchantId(merchantId)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .status(PaymentStatus.CREATED)
                .build();

       payment= paymentRepository.save(payment);

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .merchantId(payment.getMerchantId())
                .methodDetails(payment.getMethodDetails())
                .amount(payment.getAmount())
                .paymentMethod(payment.getMethod())
                .orderId(order.getId())
                .build();
        paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult paymentResult = paymentGatewayRouter.initiatePayment(paymentRequest);

        switch (paymentResult){
            case PaymentResult.Pending pending -> {
                payment.setStatus(PaymentStatus.PENDING);
                payment.setProcessorReference(pending.registerReference());
            }
            case PaymentResult.Failure failure -> {
             //   payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
                payment.setErrorCode(failure.ErrorCode());
                payment.setErrorDescription(failure.ErrorDescription());

                //throw new BusinessRulesViolationException(failure.ErrorCode(), failure.ErrorDescription());
            }
            case PaymentResult.Success success -> {
                payment.setStatus(PaymentStatus.AUTHORIZED);
                payment.setProcessorReference(success.processorReference());
                payment.setBankReference(success.bankReference());
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        // TODO send  outbox(Kafka event)
        return orderMapper.toResponse(payment);

    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public PaymentResponse capturePayment(UUID merchantId, UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND", "Payment not found for the given paymentId and merchantId"));

        // TODO:  state machine
        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
       PaymentResult paymentResult =  paymentGatewayRouter.capture(payment.getMethod(),paymentId);

       if(paymentResult instanceof PaymentResult.Success(String processorReference, String bankReference)) {
           paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
           payment.setProcessorReference(processorReference);
           payment.setBankReference(bankReference);
           payment.setCapturedAt(Instant.now());
       } else if (paymentResult instanceof PaymentResult.Failure(String errorCode, String errorDescription)) {
           paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAILURE);
           payment.setErrorCode(errorCode);
           payment.setErrorDescription(errorDescription);

       }
      payment= paymentRepository.save(payment);
       // TODO send  outbox(Kafka event)

        return orderMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("PAYMENT_NOT_FOUND", "Payment not found for the given paymentId"));
        if(payment.getStatus()!= PaymentStatus.AUTHORIZING){
            log.warn("Payment  is not Authorized {}",payment.getStatus()
            );
            return;
        }

        OrderRecord order = payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(Instant.now());

            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
            PaymentResult capturedResult = paymentGatewayRouter.capture(payment.getMethod(), paymentId);
            if(capturedResult instanceof PaymentResult.Success(String processorReference, String bankReference)) {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
                payment.setProcessorReference(processorReference);
                payment.setBankReference(bankReference);
                payment.setCapturedAt(Instant.now());
                order.setStatus(OrderStatus.PAID);
            } else if (capturedResult instanceof PaymentResult.Failure(String code, String description)) {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAILURE);
                payment.setErrorCode(code);
                payment.setErrorDescription(description);

            }

        }else {

            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_FAILURE);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);

        }
       paymentRepository.save(payment);
        orderRepository.save(order);

    }

    @Override
    public List<PaymentResponse> getAllPayments(UUID merchantId) {
        List<Payment> payments = paymentRepository.findByMerchantId(merchantId);
        return payments.stream().map(orderMapper::toResponse).toList();
    }
}
