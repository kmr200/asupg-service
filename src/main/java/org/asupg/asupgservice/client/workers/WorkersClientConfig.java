package org.asupg.asupgservice.client.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class WorkersClientConfig {

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new WorkersClientErrorDecoder(objectMapper);
    }

}
