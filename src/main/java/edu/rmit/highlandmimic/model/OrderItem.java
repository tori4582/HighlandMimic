package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderItem {

    private String productId;
    private Integer quantity;
    private List<String> toppingIds;
    private String selectedSize;

}
