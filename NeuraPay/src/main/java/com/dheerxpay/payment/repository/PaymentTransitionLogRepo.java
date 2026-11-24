package com.dheerxpay.payment.repository;

import com.dheerxpay.payment.entities.PaymentTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentTransitionLogRepo extends JpaRepository<PaymentTransitionLog, UUID> {
    List<PaymentTransitionLog> findAllByPayment_MerchantId(UUID merchantId);

    List<PaymentTransitionLog> findAllByPayment_IdAndPayment_MerchantId(UUID paymentId, UUID merchantId);

    java.util.Optional<PaymentTransitionLog> findByIdAndPayment_MerchantId(UUID id, UUID merchantId);
}
