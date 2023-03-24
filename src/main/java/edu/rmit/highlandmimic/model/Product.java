package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.jsoup.nodes.Element;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Data
@Builder
public class Product {

    @Id
    private String productId;

    @NonNull
    private String productName;

    private String productUrl;
    private Long price;
    private String currency;
    private String imageUrl;
    private String description;
    private Map<String, Long> upsizeOptions;
    private Map<String, Long> toppingOptions;
}
