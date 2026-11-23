package com.dheerxpay.payment.simulator;


import com.dheerxpay.common.enums.ChaosMode;
import com.dheerxpay.common.enums.PaymentStatus;
import com.dheerxpay.common.utility.RandomizerUtil;
import com.dheerxpay.payment.entities.Payment;
import com.dheerxpay.payment.repository.PaymentRepository;
import com.dheerxpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallBackSimulator {

     private final PaymentRepository paymentRepository;
     private final PaymentService paymentService;
     private final SimulatorConfig simulatorConfig;

     @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms: 5000}")
     private void  processCallBack(){
         Instant globalWindow = Instant.now().minusSeconds(1);
         List<Payment> candidates  = paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);
         if(candidates.isEmpty())return;

         for(Payment payment:candidates){
             simulateCallback(payment);
         }


     }

    private void simulateCallback(Payment payment) {
         // processing payment
        SimulatorConfig.MethodConfig methodConfig = simulatorConfig.getConfig(payment.getMethod());

        Instant dueAt  = dueAt(payment,methodConfig);
        if(Instant.now().isBefore(dueAt))return;
        ChaosMode chaosMode = simulatorConfig.getChaosMode();

        switch (chaosMode){
            case FAIL -> resolve(payment,false);
            case SUCCESS -> resolve(payment,true);
            case NORMAL,SLOW -> resolve(payment,shouldApprove(payment,methodConfig));
            case TIMEOUT -> log.debug("payment time out");

        }

    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodConfig methodConfig) {
        return methodConfig.getSuccessRate() < Math.abs(payment.getId().hashCode()) % 100;
    }

    private void resolve(Payment payment,boolean approve){
         if(approve) {
             String bankRef = "SIM_BANK_REF"+ RandomizerUtil.randomBase64(16);
             paymentService.resolveAuthorization(payment.getId(),true,bankRef,null,null);
         }else {
             paymentService.resolveAuthorization(payment.getId(),false,null,"SIM_BANK_REF_ERROR","bank declined the payment");
         }
    }

    private Instant dueAt(Payment payment, SimulatorConfig.MethodConfig methodConfig) {
         int range = methodConfig.getMaxDelaySeconds()- methodConfig.getMinDelaySeconds();
         int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);
         if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
             delaySeconds*=2;
         }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }

}
