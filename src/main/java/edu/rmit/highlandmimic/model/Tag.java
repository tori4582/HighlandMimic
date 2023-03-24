package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("tags")
public class Tag {

    @Id
    private String tagId;

    private String tagName;
    private String tagDescription;
    private String tagColorCode;
}
