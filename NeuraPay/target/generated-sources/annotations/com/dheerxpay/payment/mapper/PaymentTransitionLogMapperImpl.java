package com.dheerxpay.payment.mapper;

import com.dheerxpay.operations.dto.PaymentTransitionLogDto;
import com.dheerxpay.payment.entities.Payment;
import com.dheerxpay.payment.entities.PaymentTransitionLog;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-24T14:44:34+0530",
    comments = "version: 1.6.3, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class PaymentTransitionLogMapperImpl implements PaymentTransitionLogMapper {

    @Override
    public PaymentTransitionLogDto toDto(PaymentTransitionLog paymentTransitionLog) {
        if ( paymentTransitionLog == null ) {
            return null;
        }

        PaymentTransitionLogDto paymentTransitionLogDto = new PaymentTransitionLogDto();

        paymentTransitionLogDto.setPaymentId( paymentTransitionLogPaymentId( paymentTransitionLog ) );
        paymentTransitionLogDto.setCreatedBy( paymentTransitionLog.getCreatedBy() );
        paymentTransitionLogDto.setUpdatedBy( paymentTransitionLog.getUpdatedBy() );
        paymentTransitionLogDto.setId( paymentTransitionLog.getId() );
        paymentTransitionLogDto.setFromStatus( paymentTransitionLog.getFromStatus() );
        paymentTransitionLogDto.setToStatus( paymentTransitionLog.getToStatus() );
        paymentTransitionLogDto.setEvent( paymentTransitionLog.getEvent() );
        paymentTransitionLogDto.setActor( paymentTransitionLog.getActor() );
        paymentTransitionLogDto.setReason( paymentTransitionLog.getReason() );
        paymentTransitionLogDto.setOccurredAt( paymentTransitionLog.getOccurredAt() );

        return paymentTransitionLogDto;
    }

    private UUID paymentTransitionLogPaymentId(PaymentTransitionLog paymentTransitionLog) {
        Payment payment = paymentTransitionLog.getPayment();
        if ( payment == null ) {
            return null;
        }
        return payment.getId();
    }
}
