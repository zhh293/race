package org.example.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.session.StandardSession;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Cookie;
import org.springframework.boot.web.servlet.server.Session;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Enumeration;
import java.util.UUID;

@Data
@AllArgsConstructor
@Slf4j
public class AiUtil {
    private String apiKey;
    private String apiKey1;
    private String deepSeekUrl;
    private String TongYingUrl;
    private String appId;
    private String appKey;
    private String httpMethod;
    private String httpUrl;
    private String requestUrl;

    private static String calculateSignature(
            String httpMethod,
            String httpUri,
            String canonicalQueryString,
            String appId,
            long timestamp,  // 输入为 long 类型
            String nonce,
            String appKey) throws Exception {

        // 显式将 timestamp 转换为字符串
        String timestampStr = String.valueOf(timestamp);

        // 构建签名字符串（所有参数均为字符串）
        String signingString = String.join("\n",
                httpMethod.toUpperCase(),       // POST
                httpUri,                        // /vivogpt/completions
                canonicalQueryString,           // requestId=xxx
                appId,                          // "2025158089"
                timestampStr,                   // "1745768181"（显式转换）
                "x-ai-gateway-app-id:" + appId, // 头部字段
                "x-ai-gateway-timestamp:" + timestampStr, // 使用转换后的字符串
                "x-ai-gateway-nonce:" + nonce
        );

        // 打印调试信息（确保 timestamp 为字符串形式）
        System.out.println("Timestamp String: " + timestampStr);
        System.out.println("Signing String:\n" + signingString);

        // 计算 HMAC-SHA256（不变）
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(appKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hashBytes = sha256Hmac.doFinal(signingString.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public String getString(String message, String sessionId) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //生成八位随机字符串
        final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(8);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            int randomIndex = secureRandom.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(randomIndex));
        }
        String string = sb.toString();
        UUID requestId = UUID.randomUUID();
        URIBuilder uriBuilder = new URIBuilder(requestUrl);
        uriBuilder.addParameter("requestId", requestId.toString());
        URI build = uriBuilder.build();
        HttpPost httpPost = new HttpPost(build);
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        httpPost.setHeader("X-AI-GATEWAY-APP-ID",appId);
        long epochSecond = Instant.now().getEpochSecond();
        httpPost.setHeader("X-AI-GATEWAY-TIMESTAMP",epochSecond+"");
        httpPost.setHeader("X-AI-GATEWAY-NONCE",string);
        String canonicalQueryString="requestId="+requestId;
        String s = calculateSignature(httpMethod, httpUrl, canonicalQueryString, appId, epochSecond, string, appKey);
        httpPost.setHeader("X-AI-GATEWAY-SIGNATURE",s);
        httpPost.setHeader("X-AI-GATEWAY-SIGNED-HEADERS","x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce");
        String systemPrompt="你是一个生活小贴士，专门用来为别人做每一天的生活规划的。";
        String escapedMessage = JSON.toJSONString(message, SerializerFeature.WriteNonStringValueAsString);
        String requestBody = "{" +
                "\"messages\":[" +
                "{\"role\":\"user\",\"content\":\"你好呀\"}," +
                "{\"role\":\"assistant\",\"content\":\"你好！我是vivo助手，有什么可以帮助你的吗？\"}," +
                "{\"role\":\"assistant\",\"content\":\"根据天气预报，今天天气晴朗，气温25℃，非常适合户外活动。\"}," +
                "{\"role\":\"user\",\"content\":"+escapedMessage+"}" +
                "]," +
                "\"model\":\"vivo-BlueLM-TB-Pro\"," +
                "\"sessionId\":\"" + sessionId + "\"," +
                "\"systemPrompt\":\"" + systemPrompt + "\"," +
                "\"extra\":{" +
                "\"temperature\":0.9," +
                "\"top_p\":0.7," +
                "\"top_k\":50," +
                "\"max_new_tokens\":2048," +
                "\"repetition_penalty\":1.02" +
                "}" +
                "}";
        String requestBody1 = "{" +
                "\"messages\":[" +
                "{\"role\":\"user\",\"content\":\"你好呀\"}," +
                "{\"role\":\"assistant\",\"content\":\"你好！我是vivo助手，有什么可以帮助你的吗？\"}," +
                "{\"role\":\"assistant\",\"content\":\"根据天气预报，今天天气晴朗，气温25℃，非常适合户外活动。\"}," +
                "{\"role\":\"user\",\"content\":\""+message+"\"}" +
                "]," +
                "\"model\":\"vivo-BlueLM-TB-Pro\"," +
                "\"sessionId\":\"" + sessionId + "\"," +
                "\"systemPrompt\":\"" + systemPrompt + "\"," +
                "\"extra\":{" +
                "\"temperature\":0.9," +
                "\"top_p\":0.7," +
                "\"top_k\":50," +
                "\"max_new_tokens\":2048," +
                "\"repetition_penalty\":1.02" +
                "}" +
                "}";
        System.out.println("我到这里了");
        System.out.println(requestBody);
        System.out.println(requestBody1);
        httpPost.setEntity(new StringEntity(requestBody1, StandardCharsets.UTF_8));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(entity));
            JSONObject result = jsonObject.getJSONObject("data");
            return result.getString("content");
        }else{
            System.out.println(response.getStatusLine().getStatusCode());
            return null;
    }











































    /*public String getdeepSeek(String message) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(deepSeekUrl);
        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        String requestBody = "{\"model\": \"deepseek-reasoner\", \"messages\": [" +
                "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                "{\"role\": \"user\", \"content\":\""+message+"\" }" +
                "], \"temperature\": 0.7}";
        StringEntity entity = new StringEntity(requestBody, "utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse execute = httpclient.execute(httpPost);
        int statusCode = execute.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            throw new IOException(execute.getStatusLine().getReasonPhrase());
        }
        HttpEntity entity1 = execute.getEntity();
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity1));
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("choices"));
        StringBuffer sb = new StringBuffer();
        for (Object object : jsonArray) {
            JSONObject json = (JSONObject) object;
            JSONObject message1 = json.getJSONObject("message");
            String string = message1.getString("content");
            sb.append(string);
        }
        return sb.toString();
    }*/
    //剩余部分自己补全即可，套路一模一样，哈哈哈哈哈哈哈哈哈哈哈哈，太爽啦

}
}
