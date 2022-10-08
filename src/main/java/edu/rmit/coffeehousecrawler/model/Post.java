package edu.rmit.coffeehousecrawler.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Post {
    private String postUrl;
    private String title;
    private String imageUrl;
    private Integer page;
    private String collectionName;
    private String uploadedDuration;
    private String shortenedContent;
    private String articleContent;
}
