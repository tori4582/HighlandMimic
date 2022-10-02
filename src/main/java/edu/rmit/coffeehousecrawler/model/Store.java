package edu.rmit.coffeehousecrawler.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Store {

    private String groupLocation;
    private String groupLocationId;
    private String address;
    private final String openingHour = "07:00";
    private final String closingHour = "22:00";
    private String latitude;
    private String longitude;
    private String imageUrl;
    private String googleMapUrl;

    public static String toGoogleMapUrl(String latlng) {
        return "https://www.google.com/maps/search/?api=1&query=" + latlng + "&";
    }

}
