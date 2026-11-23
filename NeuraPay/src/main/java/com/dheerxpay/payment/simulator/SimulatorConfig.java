package com.dheerxpay.payment.simulator;


import com.dheerxpay.common.enums.ChaosMode;
import com.dheerxpay.common.enums.PaymentMethod;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
public class SimulatorConfig {
    private Integer pollIntervalMs=2000;

    private ChaosMode chaosMode = ChaosMode.NORMAL;

    private Map<String, MethodConfig> methods = new HashMap<>();

    public SimulatorConfig.MethodConfig getConfig(PaymentMethod method){
        return methods.getOrDefault(method.name(),new MethodConfig());
    }

    @Data
    public static class MethodConfig {

        private Integer minDelaySeconds=1;

        private Integer maxDelaySeconds=5;

        private Integer successRate=80;
    }
}
