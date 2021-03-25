package com.sipp.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sipp.authentication.RedditHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class Request {

    private static Header[] headers;

    static {
        try {
            headers = RedditHeader.getRedditHeaders();
        } catch (IOException e) {
            log.error("Exception while loading headers");
            e.printStackTrace();
        }
    }

    public enum PostCategory {
        HOT, RISING, TOP, CONTROVERSIAL, NEW;
    }

    static void prettyPrint(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
    }

    public static String getCommentsForPost(String postPermalink) throws IOException{
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://oauth.reddit.com" + postPermalink);
            request.setHeaders(headers);

            return executeRequestReturnResponse(client, request);
        }
    }

    private static String executeRequestReturnResponse(CloseableHttpClient client, HttpGet request) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.execute(request).getEntity().getContent()))){
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    public static String getListing(String subreddit, PostCategory postCategory) throws IOException, URISyntaxException {
        switch (postCategory) {
            case HOT:
                return getPosts(subreddit, "/hot");
            case TOP:
                return getPosts(subreddit, "/top");
            case RISING:
                return getPosts(subreddit, "/rising");
            case CONTROVERSIAL:
                return getPosts(subreddit, "/controversial");
            case NEW:
                return getPosts(subreddit, "/new");
        }
        throw new IllegalArgumentException("None of the passed values matched values in the switch for post categories. Check if all values in the PostCategory enum are in the switch statement");
    }

    private static String getPosts(String subreddit, String suffix) throws IOException, URISyntaxException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://oauth.reddit.com/r/" + subreddit.toLowerCase() + suffix);
            URI uri = new URIBuilder(request.getURI())
                    .addParameter("limit", "100")
                    .build();
            request.setHeaders(headers);
            request.setURI(uri);

            return executeRequestReturnResponse(client, request);
        }
    }


}
