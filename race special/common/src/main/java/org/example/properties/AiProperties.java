package org.example.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "race.deepseek")
public class AiProperties {
    private String apiKey;
    private String apiKey1;
    private String deepSeekUrl;
    private String TongYingUrl;
    private String appId;
    private String appKey;
    private String httpMethod;
    private String httpUrl;
    private String requestUrl;
}
