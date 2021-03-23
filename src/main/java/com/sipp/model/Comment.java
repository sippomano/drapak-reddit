package com.sipp.model;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Comment {

    private String author;
    private String text;
    private String permalink;
    private String subreddit;
    private String postPermalink;
    private int awardsCount;
    private int score;
    private long creationTime;
    private Comment parent;

}
