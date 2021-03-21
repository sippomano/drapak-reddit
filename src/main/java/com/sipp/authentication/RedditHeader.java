package com.sipp.authentication;

import com.sipp.config.AppProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;

@Slf4j
public class RedditHeader {

    public static Header[] getRedditHeaders() throws IOException {
       Header userAgent = new BasicHeader("User-Agent", AppProperties.getProperty("userAgent"));
       Header token = new BasicHeader("Authorization", Token.getTokenHeader(false));
       log.info("headers retrieved: " + userAgent.getName() + ": " + userAgent.getValue() + "\n" +
                token.getName() + ": " + token.getValue());
       return new Header[]{userAgent, token};
    }
}
