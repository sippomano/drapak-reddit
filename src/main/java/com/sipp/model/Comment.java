package com.sipp.model;

import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Comment {

    private String author;
    private String text;
    @EqualsAndHashCode.Include
    private String permalink;
    private String subreddit;
    private String postPermalink;
    private int awardsCount;
    private int score;
    private long creationTime;
    private String parentPermalink;

}
