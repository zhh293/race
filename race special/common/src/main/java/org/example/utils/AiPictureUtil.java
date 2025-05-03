package org.example.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiPictureUtil {
    private String appId;
    private String appKey;
    private String httpMethod;
    private String httpUrl;
    private String requestUrl;
}
