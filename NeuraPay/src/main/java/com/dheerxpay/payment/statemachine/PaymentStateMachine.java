package com.dheerxpay.payment.statemachine;

import com.dheerxpay.common.enums.PaymentEvent;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.common.exceptions.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStateMachine {

    public record StateTransition(PaymentStatus from, PaymentEvent event) {
    }

    final static Map<StateTransition, PaymentStatus> TRANSITIONS = Map.ofEntries(
            transition(PaymentStatus.CREATED, PaymentEvent.AUTHORIZE_ATTEMPT, PaymentStatus.AUTHORIZING),
            transition(PaymentStatus.INITIATED, PaymentEvent.AUTHORIZE_ATTEMPT, PaymentStatus.AUTHORIZING),
            transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_SUCCESS, PaymentStatus.AUTHORIZED),
            transition(PaymentStatus.PENDING, PaymentEvent.AUTHORIZE_SUCCESS, PaymentStatus.AUTHORIZED),
            transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_FAILURE, PaymentStatus.FAILED),
            transition(PaymentStatus.PENDING, PaymentEvent.AUTHORIZE_FAILURE, PaymentStatus.FAILED),
            transition(PaymentStatus.AUTHORIZING, PaymentEvent.PAYMENT_FAILED, PaymentStatus.FAILED),
            transition(PaymentStatus.CREATED, PaymentEvent.PAYMENT_FAILED, PaymentStatus.FAILED),
            transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_REQUEST, PaymentStatus.CAPTURING),
            transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_SUCCESS, PaymentStatus.CAPTURED),
            transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_FAILURE, PaymentStatus.AUTHORIZED),
            transition(PaymentStatus.CREATED, PaymentEvent.CANCEL_SUCCESS, PaymentStatus.CANCELLED),
            transition(PaymentStatus.INITIATED, PaymentEvent.CANCEL_SUCCESS, PaymentStatus.CANCELLED),
            transition(PaymentStatus.AUTHORIZING, PaymentEvent.CANCEL_SUCCESS, PaymentStatus.CANCELLED),
            transition(PaymentStatus.PENDING, PaymentEvent.CANCEL_SUCCESS, PaymentStatus.CANCELLED),
            transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_EXPIRED, PaymentStatus.AUTHORIZATION_EXPIRED),
            transition(PaymentStatus.CAPTURED, PaymentEvent.SETTLE, PaymentStatus.SETTLED),
            transition(PaymentStatus.CAPTURED, PaymentEvent.REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.SETTLED, PaymentEvent.REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.PARTIALLY_REFUNDED, PaymentEvent.REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.REFUNDING, PaymentEvent.REFUND_SUCCESS, PaymentStatus.REFUNDED),
            transition(PaymentStatus.REFUNDING, PaymentEvent.REFUND_FAILURE, PaymentStatus.CAPTURED),
            transition(PaymentStatus.CAPTURED, PaymentEvent.PARTIAL_REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.SETTLED, PaymentEvent.PARTIAL_REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.PARTIALLY_REFUNDED, PaymentEvent.PARTIAL_REFUND_INITIATED, PaymentStatus.REFUNDING),
            transition(PaymentStatus.REFUNDING, PaymentEvent.PARTIAL_REFUND_SUCCESS, PaymentStatus.PARTIALLY_REFUNDED),
            transition(PaymentStatus.REFUNDING, PaymentEvent.PARTIAL_REFUND_FAILURE, PaymentStatus.CAPTURED)
        );

    private static Map.Entry<StateTransition, PaymentStatus> transition(
            PaymentStatus from,
            PaymentEvent event,
            PaymentStatus to
    ) {
        return Map.entry(new StateTransition(from, event), to);
    }

    public PaymentStatus transition(PaymentStatus currentState, PaymentEvent event) {
        PaymentStatus next = TRANSITIONS.get(new StateTransition(currentState, event));
        if (next == null) {
            throw new InvalidStateTransitionException(currentState.name(), event.name());
        }
        return next;
    }
}
