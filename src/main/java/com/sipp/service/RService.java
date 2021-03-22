package com.sipp.service;

import com.sipp.model.Comment;
import com.sipp.model.Post;
import com.sipp.request.Requests;
import com.sipp.request.ResponseParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class RService {

    public static void fetchData() throws IOException, URISyntaxException, InterruptedException {
        List<Post> posts = ResponseParser.parsePostList(Requests.getHot("stocks"));

        for (Post post : posts) {
            List<Comment> comments = ResponseParser.parseCommentTree(Requests.getCommentsForPost(post.getPermalink()));
            log.info("Number of comments: " + comments.size() + " for post: " + post.getPermalink());
            post.setComments(comments);
            //no more than 60 requests per minute - api rules
            Thread.sleep(1200);
        }
    }
}
