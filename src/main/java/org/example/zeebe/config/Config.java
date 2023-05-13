package org.example.zeebe.config;

import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public ZeebeClient zeebeClient() {
        final ZeebeClient zeebeClient = ZeebeClient.newClientBuilder()
                .gatewayAddress("127.0.0.1:26500")
                //Без этой настройки падал SSL
                .usePlaintext()
                .build();
        return zeebeClient;
    }
}
