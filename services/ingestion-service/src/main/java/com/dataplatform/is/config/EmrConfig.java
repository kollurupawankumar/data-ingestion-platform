package com.dataplatform.is.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;

@Configuration
public class EmrConfig {

    @Bean
    public EmrClient emrClient() {
        return EmrClient.builder()
                .region(Region.AP_SOUTH_1) // change if needed
                .build();
    }
}
