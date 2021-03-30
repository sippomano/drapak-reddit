package com.sipp.processing;

import com.sipp.config.TickerLoader;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class WordCounter {

    public static Map<String, Long> getWordCount(String text) {
        Map<String, Long> counted = createCleanWordListFromText(text).stream()
                .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));
        log.info("counted: " + counted.toString());
        return counted;
    }

    public static Map<String, Long> getWordCountTickers(String text) throws IOException {
        List<String> tickers = TickerLoader.getTickers();
        Map<String, Long> counted = createCleanWordListFromText(text).stream()
                .map(s -> s.replace("$", ""))
                .filter(tickers::contains)
                .collect(Collectors.groupingBy((String s) -> s, Collectors.counting()));
        log.info("counted: " + counted.toString());
        return counted;
    }

    private static List<String> createCleanWordListFromText(String text) {
        Collection<String> emojis = EmojiParser.extractEmojis(text)
                .stream()
                .map(s -> s.length() <= 2 ? s : s.substring(0, 2))
                .collect(Collectors.toList());
        text = EmojiParser.removeAllEmojis(text);
        List<String> words = Arrays.stream(text.replaceAll("\\\\n", " ")
                .split(" "))
                    .filter(s -> !s.contains("http"))
                    .map(s -> s.trim()
                            .replaceAll("\\W|\\d", ""))
                    .filter(s -> !s.matches("\\s*") && !s.isEmpty())
                    .collect(Collectors.toList());
        log.info("list of clean words: " + words + " emojis: " + emojis);
        words.addAll(emojis);
        return words;
    }
}
