package com.sipp.service;

import com.sipp.config.AppProperties;
import com.sipp.dao.PostDao;
import com.sipp.dao.sql.CommentDaoSql;
import com.sipp.dao.sql.PostDaoSql;
import com.sipp.model.Comment;
import com.sipp.model.Post;
import com.sipp.request.Request;
import com.sipp.request.ResponseParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class RService {

    public static void fetchData() throws IOException, URISyntaxException, InterruptedException {
        PostDao postDao = PostDaoSql.getInstance();
        CommentDaoSql commentDao = CommentDaoSql.getInstance();

        //loop all subreddits- list in properties
        for (String subreddit : AppProperties.getProperty("subreddits").split(",")) {
            Set<Post> allSubredditPosts= new HashSet<>();
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
            }
        }
    }
