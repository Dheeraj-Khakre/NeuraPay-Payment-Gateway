package com.dheerxpay.payment.repository;

import com.dheerxpay.payment.entities.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentTransitionLogRepo extends JpaRepository<PaymentTransitionLog, UUID> {
}
