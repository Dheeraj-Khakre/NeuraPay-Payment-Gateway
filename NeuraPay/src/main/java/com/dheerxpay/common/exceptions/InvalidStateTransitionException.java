package com.dheerxpay.common.exceptions;

public class InvalidStateTransitionException extends RuntimeException {
    private final String fromState;
    private final String toEvent;
    public InvalidStateTransitionException(  String fromState, String toEvent) {
        super("Invalid state transition: " + fromState + " -> " + toEvent);
        this.fromState = fromState;
        this.toEvent = toEvent;
    }
}
