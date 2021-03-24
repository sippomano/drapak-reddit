package com.sipp.service;

import com.sipp.dao.PostDao;
import com.sipp.dao.sql.CommentDaoSql;
import com.sipp.dao.sql.PostDaoSql;
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
        PostDao postDao = PostDaoSql.getInstance();
        CommentDaoSql commentDao = CommentDaoSql.getInstance();

        //fetch posts from reddit
        List<Post> posts = ResponseParser.parsePostList(Requests.getHot("stocks"));
        //add posts to database
        postDao.addPosts(posts);

        for (Post post : posts) {

            //fetch comments for post from reddit
            List<Comment> comments = ResponseParser.parseCommentTree(Requests.getCommentsForPost(post.getPermalink()));
            //add to database
            commentDao.addComments(comments);

            log.info("Number of comments: " + comments.size() + " for post: " + post.getPermalink());
            post.setComments(comments);

            //no more than 60 requests per minute - api rules
            Thread.sleep(1200);
        }




    }
}
