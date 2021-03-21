package com.sipp.model;

import lombok.*;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post {

    private String author;
    private String text;
    private String subreddit;
    private String permalink;
    private String title;
    private String flair;
    private int awardsCount;
    private int score;
    private long creationTime;
    private List<Comment> comments;

}
