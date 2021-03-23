package com.sipp.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipp.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public final class Token {

    private static String token;

    public static String getTokenHeader(boolean refresh) throws IOException {
        if (refresh || (token == null)) {
            try {
                generateAndUpdateToken();
            } catch (IOException e) {
                log.error("Error while retrieving token header");
                throw e;
            }
        }
        return "bearer " + token;
    }

    private static void generateAndUpdateToken() throws IOException{

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            Properties tokenProperties = loadProperties();
            String key = tokenProperties.getProperty("secretKey");
            String id = tokenProperties.getProperty("scriptId");

            String uuid = UUID.randomUUID().toString();
            HttpPost request = new HttpPost(AppProperties.getProperty("tokenUrl"));
            request.setHeader("Authorization", encodeRequest(id, key));
            request.setHeader("User-Agent", "windows:com.example.drapak:v0.0.1 (by /u/WesternBot)");

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "https://oauth.reddit.com/grants/installed_client"));
            params.add(new BasicNameValuePair("device_id", uuid));
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.execute(request).getEntity().getContent()))){
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String token = extractTokenFromJson(builder.toString());
                log.info("token: " + token );
                tokenProperties.setProperty("token", token);
                Token.token = token;
            }
        }
    }

    private static String encodeRequest(String user, String pass) {
        return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes(StandardCharsets.UTF_8));
    }

    private static String extractTokenFromJson(String jsonBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonBody);
        return root.get("access_token").asText();

    }

    private static Properties loadProperties() throws IOException{
        try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(AppProperties.getProperty("keyPath")))) {
            Properties tokenProperties = new Properties();
            tokenProperties.load(inputStream);
            log.info("Token properties loaded. Size: " + tokenProperties.size());
            return tokenProperties;
        }
    }
}
