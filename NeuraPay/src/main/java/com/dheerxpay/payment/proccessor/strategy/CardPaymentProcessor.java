package com.dheerxpay.payment.proccessor.strategy;

import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.payment.proccessor.PaymentProcessor;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorRequest;
import com.dheerxpay.payment.proccessor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentProcessor implements PaymentProcessor {

    private static final String PAN_CARD_DECLINED="40000000000000002";
    private static final String PAN_CARD_EXPIRED="40000000000000069";


    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
           String pan = request.pan();
           if(PAN_CARD_DECLINED.equals(pan)){
               log.warn("pan card has declined {}",pan);
               return PaymentProcessorResponse.Failure.builder().errorCode("PAN_CARD_DECLINED").errorDescription("Pan card has Declined").build();
           }
        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("pan card has expired {}",pan);
            return PaymentProcessorResponse.Failure.builder().errorCode("PAN_CARD_EXPIRED").errorDescription("Pan card has Expired").build();
        }

        String processorRef = "CARD_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        return PaymentProcessorResponse.Pending.builder()
                .processorReference(processorRef)
                .build();
    }
}
