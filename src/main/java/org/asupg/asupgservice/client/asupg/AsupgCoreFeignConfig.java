package org.asupg.asupgservice.client.asupg;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class AsupgCoreFeignConfig {

    @Bean
    public RequestInterceptor asupgEmptyBodyInterceptor() {
        return requestTemplate -> {
            if (requestTemplate.body() == null) {
                requestTemplate.body("{}");
            }
        };
    }

}