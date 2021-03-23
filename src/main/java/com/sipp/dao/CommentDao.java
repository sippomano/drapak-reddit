package com.sipp.dao;

import com.sipp.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    void addComments(List<Comment> comments);
    Optional<Comment> getComment(String permalink);
    Comment getComments();
    Comment getComments(long since);
    Comment getComments(long since, long until);
}
