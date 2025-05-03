package org.example.testServer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.server.ServerApplication;
import org.example.server.configuration.WebSocketConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Dictionary;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest( classes = ServerApplication.class, // 显式指定主应用类
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ServerApplicationTests {
//蓝心大模型AppID
//2025158089
//蓝心大模型AppKEY
//FIKXhKoeMNzWSCLV
    @Test
    void contextLoads() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        httppost.setHeader("Content-Type", "application/json");
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Authorization", "Bearer " + "sk-d88586a4d04744c2a0fea3a0e2841aca");
        String requestBody = "{\"model\": \"qwen-plus\", \"messages\": [" +
                "{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}," +
                "{\"role\": \"user\", \"content\":\"" + "你好，请解释一下级数的概念，详细一点" + "\" }" +
                "], \"stream\": true,\"stream_options\":{\"include_usage\":true}}";
        HttpEntity requestEntity = new StringEntity(requestBody, StandardCharsets.UTF_8);
        httppost.setEntity(requestEntity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == 200) {
            InputStream content = response.getEntity().getContent();
            InputStreamReader reader = new InputStreamReader(content, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            String length;
            while ((length = br.readLine()) != null) {
                if (length.startsWith("data: ")) {
                    String jsonPart = length.substring(6).trim();
                    if ("[DONE]".equals(jsonPart)) {
                        break;
                    }
                    JSONObject jsonObject = JSONObject.parseObject(jsonPart);
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    for (Object object : jsonArray) {
                        JSONObject json = (JSONObject) object;
                        JSONObject jsonobject = json.getJSONObject("delta");
                        String string = jsonobject.getString("content");
                        System.out.println(string);
                    }
                }
            }
        }
        response.close();
    }
    @MockBean
    private HttpSession session;
    @Test
    void test(/*Session session*/) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 构建HTTP请求
        String appId = "2025158089";
        String appKey = "FIKXhKoeMNzWSCLV";
        String httpMethod = "POST";
        //String httpUri = "/vivogpt/completions";
        String httpUri="/api/v1/task_submit";
        //String requestUrl = "https://api-ai.vivo.com.cn" + httpUri;
        String requestUrl="https://api-ai.vivo.com.cn"+httpUri;
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
        /*URIBuilder builder = new URIBuilder(requestUrl);
        builder.addParameter("requestId", requestId.toString());
        //builder.addParameter("requestId", URLEncoder.encode(requestId.toString(),StandardCharsets.UTF_8.toString()));
        URI uri = builder.build();*/
            HttpPost httpPost = new HttpPost(requestUrl);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("X-AI-GATEWAY-APP-ID","2025158089");
        long epochSecond = Instant.now().getEpochSecond();
        httpPost.setHeader("X-AI-GATEWAY-TIMESTAMP",epochSecond+"");
        httpPost.setHeader("X-AI-GATEWAY-NONCE",string);
        //String canonicalQueryString="requestId="+requestId;
        String canonicalQueryString="";
        String s = calculateSignature(httpMethod, httpUri, canonicalQueryString, appId, epochSecond, string, appKey);
        httpPost.setHeader("X-AI-GATEWAY-SIGNATURE",s);
        httpPost.setHeader("X-AI-GATEWAY-SIGNED-HEADERS","x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce");
        // 生成合法的 UUID 字符串
        when(session.getId()).thenReturn(String.valueOf(requestId));
        String id = session.getId();
        UUID uuid = UUID.fromString(id);
        /*String systemPrompt="你是一个生活小贴士，专门用来为别人做每一天的生活规划的。";
        String requestBody = "{" +
                "\"messages\":[" +
                "{\"role\":\"user\",\"content\":\"你好呀\"}," +
                "{\"role\":\"assistant\",\"content\":\"你好！我是vivo助手，有什么可以帮助你的吗？\"}," +
                "{\"role\":\"user\",\"content\":\"今天天气如何？\"}," +
                "{\"role\":\"assistant\",\"content\":\"根据天气预报，今天天气晴朗，气温25℃，非常适合户外活动。\"}," +
                "{\"role\":\"user\",\"content\":\"适合穿什么衣服？\"}" +
                "]," +
                "\"model\":\"vivo-BlueLM-TB-Pro\"," +
                "\"sessionId\":\"" + uuid + "\"," +
                "\"systemPrompt\":\"" + systemPrompt + "\"," +
                "\"extra\":{" +
                "\"temperature\":0.9," +
                "\"top_p\":0.7," +
                "\"top_k\":50," +
                "\"max_new_tokens\":2048," +
                "\"repetition_penalty\":1.02" +
                "}" +
                "}";
        // 在 httpclient.execute(httpPost) 前添加以下调试代码
        System.out.println("===== 请求调试信息 =====");
        System.out.println("Request URL: " + httpPost.getURI());
        System.out.println("Headers:");
        for (Header header : httpPost.getAllHeaders()) {
            System.out.println("  " + header.getName() + ": " + header.getValue());
        }
        System.out.println("Canonical Query String: " + canonicalQueryString);
        System.out.println("Signing String: " + s); // 在 calculateSignature 方法中添加打印
        System.out.println("Generated Signature: " + s);
        System.out.println("=======================");*/
        String requestBody = "{" +
                "\"dataId\": \"a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4\"," +
                "\"businessCode\": \"pc\"," +
                "\"userAccount\": \"openid_123456\"," +
                "\"prompt\": \"梦泪老师在偷塔\"," +
                "\"imageType\": 1," +
                "\"styleConfig\": \"4cbc9165bc615ea0815301116e7925a3\"," +
                "\"height\": 512," +
                "\"width\": 512," +
                "\"seed\": -1," +
                "\"cfgScale\": 7," +
                "\"denoisingStrength\": 0.1," +
                "\"ctrlNetStrength\": 0.5," +
                "\"steps\": 15," +
                "\"negativePrompt\": \"低分辨率, 扭曲的面部\"" +
                "}";
        httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));
        CloseableHttpResponse response = httpclient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
           /* HttpEntity entity = response.getEntity();
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
            jsonObject = jsonObject.getJSONObject("data");
            String string1 = jsonObject.getString("content");
            System.out.println(string1);*/
            HttpEntity entity = response.getEntity();
            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(entity));
            JSONObject result = jsonObject.getJSONObject("result");
            String string1 = result.getString("task_id");
            //开始查询作画任务
            response.close();
            httpclient.close();
            String httpurL="/api/v1/task_progress";
            String requestURL="https://api-ai.vivo.com.cn"+httpurL;
            CloseableHttpClient httpclient1 = HttpClients.createDefault();
            for(int i=0;i<5;i++) {
                URIBuilder uriBuilder = new URIBuilder(requestURL);
                uriBuilder.addParameter("task_id", string1);
                URI build = uriBuilder.build();
                HttpGet httpGet = new HttpGet(build);
                String canonicalQueryString1 = "task_id=" + string1;
                httpGet.setHeader("X-AI-GATEWAY-TIMESTAMP", epochSecond + "");
                httpGet.setHeader("X-AI-GATEWAY-NONCE", string);
                httpGet.setHeader("X-AI-GATEWAY-SIGNED-HEADERS", "x-ai-gateway-app-id;x-ai-gateway-timestamp;x-ai-gateway-nonce");
                String s1 = calculateSignature("GET", httpurL, canonicalQueryString1, appId, epochSecond, string, appKey);
                httpGet.setHeader("X-AI-GATEWAY-SIGNATURE", s1);
                httpGet.setHeader("Content-Type", "application/json");
                httpGet.setHeader("X-AI-GATEWAY-APP-ID", "2025158089");
                CloseableHttpResponse execute = httpclient1.execute(httpGet);
                if (execute.getStatusLine().getStatusCode() == 200) {
                    // 1. 一次性读取完整响应内容（推荐使用 EntityUtils）
                    String responseBody = EntityUtils.toString(execute.getEntity());

                    // 2. 解析完整的 JSON 对象
                    JSONObject jsonObject1 = JSONObject.parseObject(responseBody);
                    JSONObject result1 = jsonObject1.getJSONObject("result");
                    Integer status = result1.getInteger("status");
                    System.out.println(responseBody);
                    // 3. 检查任务状态
                    if (status==2) {
                        System.out.println("任务完成，响应内容：");
                        System.out.println(responseBody);
                        break;// 直接打印完整响应
                        // 此处可提取 images_url 等信息
                    } else {
                        System.out.println("任务未完成，当前状态：" + status);
                    }
                } else {
                    System.out.println("HTTP请求失败，状态码：" + execute.getStatusLine().getStatusCode());
                }
                if (i < 4) {
                    Thread.sleep(3000);
                }
            }

        }


    }
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
}
