package com.dheerxpay.payment.repository;

import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.payment.entities.OrderRecord;
import com.dheerxpay.payment.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment , UUID> {
        Collection<Payment> findByOrder(OrderRecord orderRecord);

        Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, Instant createdAt);
}
