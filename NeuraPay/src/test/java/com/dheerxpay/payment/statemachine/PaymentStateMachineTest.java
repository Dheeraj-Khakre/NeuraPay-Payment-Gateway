package com.dheerxpay.payment.statemachine;

import com.dheerxpay.common.enums.PaymentEvent;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.common.exceptions.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentStateMachineTest {

    private final PaymentStateMachine stateMachine = new PaymentStateMachine();

    @Test
    void authorizationAndCaptureFollowPaymentLifecycle() {
        assertEquals(PaymentStatus.AUTHORIZING,
                stateMachine.transition(PaymentStatus.CREATED, PaymentEvent.AUTHORIZE_ATTEMPT));
        assertEquals(PaymentStatus.AUTHORIZED,
                stateMachine.transition(PaymentStatus.AUTHORIZING, PaymentEvent.AUTHORIZE_SUCCESS));
        assertEquals(PaymentStatus.CAPTURING,
                stateMachine.transition(PaymentStatus.AUTHORIZED, PaymentEvent.CAPTURE_REQUEST));
        assertEquals(PaymentStatus.CAPTURED,
                stateMachine.transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_SUCCESS));
    }

    @Test
    void failedCaptureReturnsPaymentToAuthorized() {
        assertEquals(PaymentStatus.AUTHORIZED,
                stateMachine.transition(PaymentStatus.CAPTURING, PaymentEvent.CAPTURE_FAILURE));
    }

    @Test
    void invalidTransitionIsRejected() {
        assertThrows(InvalidStateTransitionException.class,
                () -> stateMachine.transition(PaymentStatus.CAPTURED, PaymentEvent.CAPTURE_REQUEST));
    }
}
