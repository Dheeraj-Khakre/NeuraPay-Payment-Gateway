package com.dheerxpay.payment.statemachine;


import com.dheerxpay.common.enums.PaymentActor;
import com.dheerxpay.common.enums.PaymentEvent;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.payment.entities.Payment;
import com.dheerxpay.payment.entities.PaymentTransitionLog;
import com.dheerxpay.payment.repository.PaymentTransitionLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepo paymentTransitionLogRepo;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent){
        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(),paymentEvent);
        payment.setStatus(next);
        PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .toStatus(next)
                .event(paymentEvent)
                .actor(PaymentActor.SYSTEM.name()) // todo merchant  context to identify Actor
                .occurredAt(Instant.now())
                .build();
        paymentTransitionLogRepo.save(paymentTransitionLog);
        return next;
    }

}
