package com.sipp.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.sipp.model.Comment;
import com.sipp.model.Post;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ResponseParser {

    public static List<Post> parsePostList(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        Requests.prettyPrint(root.toString());
        List<Post> postList = new ArrayList<>();
        JsonNode posts = root.get("data").get("children");
            for (JsonNode current : posts) {
                current = current.get("data");
                Post post = new Post();
                post.setAuthor(current.get("author_fullname").toString());
                post.setComments(null);//"num_comments"?
                post.setPermalink(current.get("permalink").toString());
                post.setAwardsCount(current.get("all_awardings").size());
                post.setSubreddit(current.get("subreddit").toString());
                post.setText(current.get("selftext").toString());
                post.setTitle(current.get("title").toString());
                post.setScore(current.get("score").asInt());
                post.setCreationTime(current.get("created").asLong());
                post.setFlair(current.get("link_flair_text").toString());

                System.out.println(post.toString());
                postList.add(post);
            }
       return postList;
    }

    public static List<Comment> parseCommentTree(String json) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = (ArrayNode) mapper.readTree(json);
        Requests.prettyPrint(json);

        List<Comment> comments = new ArrayList<>();
        //first element in root[] is the post
        Iterator<JsonNode> topCommentsIterator = root.get(1).get("data").get("children").iterator();
        JsonNode current;
        while ((topCommentsIterator.hasNext())) {
            current = topCommentsIterator.next();
            log.info(String.valueOf(current.get("kind").asText()));
            parseCommentHelper(current.get("data"), null, comments);
        }
        log.info(String.valueOf(comments.size()));
        log.info(comments.toString());
        return comments;
    }

    private static void parseCommentHelper(JsonNode current, Comment parent, List<Comment> comments) {
        Comment comment = new Comment();

        comment.setAuthor(current.get("author_fullname").toString());
        comment.setText(current.get("body").toString());
        comment.setPermalink(current.get("permalink").toString());
        comment.setSubreddit(current.get("subreddit").toString());
        comment.setAwardsCount(current.get("all_awardings").size());
        comment.setScore(current.get("score").asInt());
        comment.setCreationTime(current.get("created").asLong());
        comment.setParent(parent);

        comments.add(comment);

        if (!current.get("replies").isEmpty()) {
            JsonNode replies = current.get("replies").get("data").get("children");
            for (JsonNode reply : replies) {
                parseCommentHelper(reply.get("data"), comment, comments);
            }
        }
    }
}