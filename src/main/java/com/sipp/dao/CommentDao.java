package com.sipp.dao;

import com.sipp.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    void addComments(List<Comment> comments);
    Optional<Comment> getComment(String permalink);
    List<Comment> getComments();
    List<Comment> getComments(long since);
    List<Comment> getComments(long since, long until);
    List<Comment> getCommentsForPost(String postPermalink);
    List<Comment> getComments(String subreddit);
}
