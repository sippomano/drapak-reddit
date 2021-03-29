package com.sipp;

import com.sipp.dao.sql.PostDaoSql;
import com.sipp.model.Post;
import com.sipp.processing.WordCounter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
       // RService.fetchData();
        List<Post> posts = PostDaoSql.getInstance().getPosts();
        Map<String, Long> allTickers = new HashMap<>();
        for (Post post : posts) {
            String allComments = post.getComments().stream().flatMap(s -> Stream.of(s.getText() + " ")).reduce(String::concat).orElse("");
            Map<String, Long> tickers = WordCounter.getWordCountTickers(post.getText() + " " + allComments);
            for (Map.Entry<String, Long> entry : tickers.entrySet()) {
                allTickers.merge(entry.getKey(), entry.getValue(), (Long::sum));
            }
            log.info("another iteration: number of tickers: " + allTickers.values().size());
        }
        allTickers.entrySet().stream()
                .sorted((e1, e2) -> (int)(e1.getValue()-e2.getValue()))
                .forEach(e -> System.out.println(e.getKey() + ": " + e.getValue()));
    }
}
