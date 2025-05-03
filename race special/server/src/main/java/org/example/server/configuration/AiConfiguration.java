package org.example.server.configuration;

import lombok.extern.slf4j.Slf4j;
import org.example.properties.AiProperties;
import org.example.utils.AiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AiConfiguration {
    @Bean
    public AiUtil aiUtil(AiProperties aiProperties) {
        return new AiUtil(aiProperties.getApiKey(),aiProperties.getApiKey1(),aiProperties.getDeepSeekUrl(),
                aiProperties.getTongYingUrl(), aiProperties.getAppId(), aiProperties.getAppKey(), aiProperties.getHttpMethod(), aiProperties.getHttpUrl(), aiProperties.getRequestUrl());
    }
}
