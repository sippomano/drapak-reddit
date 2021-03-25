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
        Request.prettyPrint(root.toString());
        List<Post> postList = new ArrayList<>();
        JsonNode posts = root.get("data").get("children");
            for (JsonNode current : posts) {
                current = current.get("data");
                Request.prettyPrint(current.toString());
                Post post = new Post();
                post.setAuthor(stripDoubleQuotes(current.get("author").toString()
                        .equals("\"[deleted]\"") ? current.get("author").toString() : current.get("author_fullname").toString()));
                post.setCommentsCount(current.get("num_comments").asInt());
                post.setPermalink(stripDoubleQuotes(current.get("permalink").toString()));
                post.setAwardsCount(current.get("all_awardings").size());
                post.setSubreddit(stripDoubleQuotes(current.get("subreddit").toString()));
                post.setText(stripDoubleQuotes(current.get("selftext").toString()));
                post.setTitle(stripDoubleQuotes(current.get("title").toString()));
                post.setScore(current.get("score").asInt());
                post.setCreationTime(current.get("created").asLong());
                post.setFlair(stripDoubleQuotes(current.get("link_flair_text").toString()));

                log.info(post.toString());
                postList.add(post);
            }
       return postList;
    }

    public static List<Comment> parseCommentTree(String json) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode root = (ArrayNode) mapper.readTree(json);
        Request.prettyPrint(json);
        String postPermalink = stripDoubleQuotes(root.get(0).get("data").get("children").get(0).get("data").get("permalink").toString());
        List<Comment> comments = new ArrayList<>();
        //first element in root[] is the post
        Iterator<JsonNode> topCommentsIterator = root.get(1).get("data").get("children").iterator();
        JsonNode current;
        while ((topCommentsIterator.hasNext())) {
            current = topCommentsIterator.next();
            log.info(String.valueOf(current.get("kind").asText()));
            if (!current.get("kind").toString().equals("\"more\"")) {
                parseCommentHelper(current.get("data"), null, comments);
            }
        }
        comments.forEach(c -> c.setPostPermalink(postPermalink));
        log.info(String.valueOf(comments.size()));
        log.info(comments.toString());
        return comments;
    }

    private static void parseCommentHelper(JsonNode current, Comment parent, List<Comment> comments) {
        Comment comment = new Comment();
        //comments deleted by author/moderator cannot be added however they preserve their responses which will be processed like top comments
        log.info("current: " + current.toString());
        boolean deletedFlag = (current.get("body").toString().equals("\"[deleted]\"") || (current.get("author").toString().equals("\"[deleted]\"")));
        if (!deletedFlag) {

            comment.setAuthor(stripDoubleQuotes(current.get("author_fullname").toString()));
            comment.setText(stripDoubleQuotes(current.get("body").toString()));
            comment.setPermalink(stripDoubleQuotes(current.get("permalink").toString()));
            comment.setSubreddit(stripDoubleQuotes(current.get("subreddit").toString()));
            comment.setAwardsCount(current.get("all_awardings").size());
            comment.setScore(current.get("score").asInt());
            comment.setCreationTime(current.get("created").asLong());
            if (parent != null) {
                comment.setParentPermalink(parent.getPermalink());
            }
            log.info("adding comment: " + comment.toString());
            comments.add(comment);
            log.info("current comment permalink: " + comment.getPermalink());

        }
        if (!current.get("replies").isEmpty()) {
            JsonNode replies = current.get("replies").get("data").get("children");
            log.info("number of replies: " + replies.size());

            for (JsonNode reply : replies) {
                //only most relevant comments will be saved. Loading all of the comments would greatly multiply the number of requests.
                //might be added in the future if found useful enough to compensate the slowdown. "more" element is used for loading these.
                if (reply.get("kind").toString().equals("\"more\"")) {
                    break;
                } else if (!deletedFlag) {
                    parseCommentHelper(reply.get("data"), comment, comments);
                } else {
                    parseCommentHelper(reply.get("data"), null, comments);
                }
            }
        }
    }

    private static String stripDoubleQuotes(String text) {
        return text.substring(1, text.length()-1);
    }
}
