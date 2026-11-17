package com.dheerxpay.payment.statemachine;

import com.dheerxpay.common.enums.PaymentEvent;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.common.exceptions.InvalidStateTransitionException;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStateMachine {

    @Builder
    public record StateTransition(PaymentStatus from, PaymentEvent event) {
    }

    final static Map<StateTransition, PaymentStatus> TRANSITIONS = Map.ofEntries(
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.INITIATED)
                        .event(PaymentEvent.AUTHORIZE_ATTEMPT)
                        .build(), PaymentStatus.AUTHORIZING),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.AUTHORIZING)
                        .event(PaymentEvent.AUTHORIZE_SUCCESS)
                        .build(), PaymentStatus.AUTHORIZED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.AUTHORIZING)
                        .event(PaymentEvent.AUTHORIZE_FAILURE)
                        .build(), PaymentStatus.FAILED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.AUTHORIZED)
                        .event(PaymentEvent.CAPTURE_REQUEST)
                        .build(), PaymentStatus.CAPTURING),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.CAPTURING)
                        .event(PaymentEvent.CAPTURE_SUCCESS)
                        .build(), PaymentStatus.CAPTURED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.CAPTURING)
                        .event(PaymentEvent.CAPTURE_FAILURE)
                        .build(), PaymentStatus.AUTHORIZED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.CAPTURED)
                        .event(PaymentEvent.CANCEL_REQUEST).build(), PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(StateTransition.builder()
                    .from(PaymentStatus.CAPTURED)
                    .event(PaymentEvent.SETTLE).build(), PaymentStatus.SETTLED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.PARTIALLY_REFUNDED)
                        .event(PaymentEvent.PARTIAL_REFUND_SUCCESS).build(), PaymentStatus.REFUNDED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.CAPTURED)
                        .event(PaymentEvent.REFUND_INITIATED).build(), PaymentStatus.REFUNDED),
            Map.entry(StateTransition.builder()
                    .from(PaymentStatus.SETTLED)
                    .event(PaymentEvent.REFUND_INITIATED).build(), PaymentStatus.REFUNDED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.CREATED)
                        .event(PaymentEvent.CANCEL_SUCCESS).build(), PaymentStatus.CANCELLED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.AUTHORIZING)
                        .event(PaymentEvent.CANCEL_SUCCESS).build(), PaymentStatus.CANCELLED),
                Map.entry(StateTransition.builder()
                        .from(PaymentStatus.AUTHORIZED)
                        .event(PaymentEvent.CAPTURE_EXPIRED).build(), PaymentStatus.CANCELLED)
        );

    public PaymentStatus transition(PaymentStatus currentState, PaymentEvent event) {
        PaymentStatus next = TRANSITIONS.get(new StateTransition(currentState, event));
        if (next == null) {
            throw new InvalidStateTransitionException( currentState.name(), event.name());
        }
        return next;
    }
}
