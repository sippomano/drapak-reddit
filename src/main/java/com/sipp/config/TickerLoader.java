package com.sipp.config;

import com.sipp.config.AppProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class TickerLoader {

    private static List<String> tickers;

    private TickerLoader() {}

    public static List<String> getTickers() throws IOException {
        if (tickers == null) {
            //TBD the file is downloaded directly from website and auto refreshed
           String tickerFilePath = AppProperties.getProperty("tickersFilePath");
           try (BufferedReader reader = new BufferedReader(new FileReader(tickerFilePath))){
               tickers = new ArrayList<>();
               String line;
               while ((line = reader.readLine()) != null) {
                   line = line.substring(0, line.indexOf(','));
                   if (line.matches("[A-Z]+")) {
                       tickers.add(line);
                   }
                   log.info(tickers.toString());
               }
           }
        }
        return Collections.unmodifiableList(tickers);
    }
}
