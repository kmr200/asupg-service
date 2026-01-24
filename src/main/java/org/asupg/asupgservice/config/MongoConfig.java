package org.asupg.asupgservice.config;

import org.asupg.asupgservice.config.converter.YearMonthReadConverter;
import org.asupg.asupgservice.config.converter.YearMonthWriteConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                List.of(
                        new YearMonthReadConverter(),
                        new YearMonthWriteConverter()
                )
        );
    }

}
