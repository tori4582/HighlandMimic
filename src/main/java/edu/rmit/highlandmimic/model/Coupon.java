package edu.rmit.highlandmimic.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coupon {

    private String id;
    private String couponName;
    private String content;
    private String imageUrl;
    private String qrUrl;
    private String dueDate;
}
