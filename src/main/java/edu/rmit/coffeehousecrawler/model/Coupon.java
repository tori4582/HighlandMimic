package edu.rmit.coffeehousecrawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {
    @JsonProperty("couponId")
    private String id;
    @JsonProperty("couponTitle")
    private String couponName;
    private String content;
    @JsonProperty("imgUrl")
    private String imageUrl;
    private String qrUrl;
    @JsonProperty("expiredDate")
    private String dueDate;
}
