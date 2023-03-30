package edu.rmit.highlandmimic.model.request;

import edu.rmit.highlandmimic.model.Order;
import edu.rmit.highlandmimic.model.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class OrderRequestEntity {

    private String userId;
    private List<OrderItem> orderItems;

    private Order.PaymentMethod paymentMethod;
    private Order.PickupOption pickupOptions;

    private List<String> couponIds;
    private String storeId;

    private String address1;
    private String address2;
    private String address3;
    private String address4;

}
