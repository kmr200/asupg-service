package org.asupg.asupgservice.client;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class WorkersClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new WorkersClientErrorDecoder();
    }

}
