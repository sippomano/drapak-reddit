package com.sipp.service;

import com.sipp.config.AppProperties;
import com.sipp.dao.PostDao;
import com.sipp.dao.sql.CommentDaoSql;
import com.sipp.dao.sql.PostDaoSql;
import com.sipp.model.Comment;
import com.sipp.model.Post;
import com.sipp.processing.WordCounter;
import com.sipp.request.Request;
import com.sipp.request.ResponseParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class RService {

    @Getter
    private static final Set<Post> postCache = new HashSet<>();
    @Getter
    private static final Set<Comment> commentCache = new HashSet<>();
    private static long lastFetchOldestPost = 0;

    public static void fetchData() throws IOException, URISyntaxException, InterruptedException {
        PostDao postDao = PostDaoSql.getInstance();
        CommentDaoSql commentDao = CommentDaoSql.getInstance();

        //loop all subreddits- list in properties
        for (String subreddit : AppProperties.getProperty("subreddits").split(",")) {
            Set<Post> allSubredditPosts = new HashSet<>();
            //loop all listings categories
            for (Request.PostCategory category : Request.PostCategory.values()) {
                //fetch posts from reddit for each category
                List<Post> posts = ResponseParser.parsePostList(Request.getListing(subreddit, category));
                log.debug("Number of posts for subreddit: " + subreddit + " category: " + category);
                allSubredditPosts.addAll(posts);
                Thread.sleep(1050);
            }
            log.debug("all posts included from the subreddit: " + subreddit + "from this read: " + allSubredditPosts.size());
            //add posts to database and loop the Set to get the comments
            postDao.addPosts(allSubredditPosts);
            for (Post post : allSubredditPosts) {

                log.debug("Adding comments for post: " + post.getPermalink());
                //fetch comments for post from reddit
                List<Comment> comments = ResponseParser.parseCommentTree(Request.getCommentsForPost(post.getPermalink()));
                //add to the database
                commentDao.addComments(comments);

                log.info("Number of comments: " + comments.size() + " for post: " + post.getPermalink());
                post.setComments(comments);

                //no more than 60 requests per minute - api rules
                Thread.sleep(1050);
            }
            lastFetchOldestPost = allSubredditPosts.stream()
                    .mapToLong(Post::getCreationTime)
                    .min()
                    .orElseThrow(() -> new IllegalStateException("The creation time in posts returned from database is empty. Unable to compare"));
        }
    }

    public static long loadDataToCache() {
        List<Post> posts = PostDaoSql.getInstance().getPosts(lastFetchOldestPost);
        log.info("Post list loaded from the database, size: " + posts.size());
        List<Comment> comments = CommentDaoSql.getInstance().getComments(lastFetchOldestPost);
        log.info("Comment list loaded from the database, size: " + comments.size());
        synchronized (postCache) {
            postCache.removeAll(posts);
            postCache.addAll(posts);
        }
        synchronized (commentCache) {
            commentCache.removeAll(comments);
            commentCache.addAll(comments);
        }
        long timestamp = System.currentTimeMillis();
        log.info("cache updated at time: " + timestamp);
        return timestamp;
    }


    public static Map<String, Long> getTickerCount() throws IOException {
        List<Post> posts = PostDaoSql.getInstance().getPosts();
        Map<String, Long> allTickers = new HashMap<>();
        for (Post post : posts) {
            String allComments = post.getComments().stream().flatMap(s -> Stream.of(s.getText() + " "))
                    .reduce(String::concat)
                    .orElse("");
            Map<String, Long> tickers = WordCounter.getWordCountTickers(post.getText() + " " + allComments);
            for (Map.Entry<String, Long> entry : tickers.entrySet()) {
                allTickers.merge(entry.getKey(), entry.getValue(), (Long::sum));
            }
            log.info("another iteration: number of tickers: " + allTickers.values().size());
        }
        //logging
        allTickers.entrySet().stream()
                .sorted((e1, e2) -> (int)(e1.getValue()-e2.getValue()))
                .forEach(e -> log.info(e.getKey() + ": " + e.getValue()));
        return allTickers;
    }

    public static Map<String, Long> getWordCount() throws IOException {
        List<Post> posts = PostDaoSql.getInstance().getPosts();
        Map<String, Long> allWords = new HashMap<>();
        for (Post post : posts) {
            String allComments = post.getComments().stream().flatMap(s -> Stream.of(s.getText() + " "))
                    .reduce(String::concat)
                    .orElse("");
            Map<String, Long> tickers = WordCounter.getWordCount(post.getText() + " " + allComments);
            for (Map.Entry<String, Long> entry : tickers.entrySet()) {
                allWords.merge(entry.getKey(), entry.getValue(), (Long::sum));
            }
            log.info("another iteration: number of tickers: " + allWords.values().size());
        }
        //logging
        allWords.entrySet().stream()
                .sorted((e1, e2) -> (int)(e1.getValue()-e2.getValue()))
                .forEach(e -> log.info(e.getKey() + ": " + e.getValue()));
        return allWords;
    }
}




