package com.sipp.model;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Post {

    private String author;
    private String text;
    private String subreddit;
    @EqualsAndHashCode.Include
    private String permalink;
    private String title;
    private String flair;
    private int commentsCount;//contains unloaded comments, explanation in ResponseParser.parseCommentHelper()
    private int awardsCount;
    private int score;
    private long creationTime;
    private List<Comment> comments;


}
