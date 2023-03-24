package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("posts")
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
