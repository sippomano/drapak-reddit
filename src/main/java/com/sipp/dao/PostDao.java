package com.sipp.dao;

import com.sipp.model.Post;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PostDao {

    void addPosts(List<Post> posts);
    Optional<Post> getPost(String permalink);
    List<Post> getPosts();
    List<Post> getPosts(long since);
    List<Post> getPosts(long since, long until);
}
