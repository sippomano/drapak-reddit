package com.sipp.config;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class AppProperties {

    private static final Properties generalProperties;
    private static final String configPath = "src/main/resources/config.properties";

    static {
        generalProperties = new Properties();
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(configPath))) {
            generalProperties.load(inputStream);
            log.info("General properties loaded. Size: " + generalProperties.size());

        } catch (IOException e) {
            log.error("Error occurred while loading general properties. Properties will be empty");
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return generalProperties.getProperty(key);
    }

    public static Map<String, String> getAllProperties() {
        return generalProperties.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap((e) -> (String) e.getKey(), (e) -> (String) e.getValue()));
    }
}
