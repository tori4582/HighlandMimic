package edu.rmit.coffeehousecrawler.model;

import lombok.Builder;
import lombok.Data;
import org.jsoup.nodes.Element;

import java.util.Map;

@Data
@Builder
public class Product {

    private String name;
    private String productUrl;
    private Long price;
    private String currency;
    private String imageUrl;
    private String collectionName;
    private String description;
//    private Map<String, Long> upsizeOptions;
//    private Map<String, Long> toppingOptions;
}
