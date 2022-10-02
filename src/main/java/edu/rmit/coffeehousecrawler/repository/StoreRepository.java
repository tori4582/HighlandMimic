package edu.rmit.coffeehousecrawler.repository;

import edu.rmit.coffeehousecrawler.common.JSoupWebCrawler;
import edu.rmit.coffeehousecrawler.model.Store;
import lombok.SneakyThrows;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StoreRepository {

    public static final String SELECTOR_STORE_GROUP = "div.list_all_store_item";
    public static final String SELECTOR_STORE_ITEM = "div.list_all_store>a";

    private final Resource resourceFile = new ClassPathResource("/static/stores.html");

    @SneakyThrows
    public List<Store> fetchAllStores() {

        System.err.println(resourceFile.getFile().getAbsolutePath());

        List<Element> groupLocations = retrieveHtmlFromResource().select(SELECTOR_STORE_GROUP);
        List<Store> stores = new ArrayList<>();

        for (Element location : groupLocations) {
            List<Element> locationStores = location.select(SELECTOR_STORE_ITEM);
            stores.addAll(
                    locationStores.stream().map(
                            s -> {
                                String latlng = extractGeographicLocation(s);

                                return Store.builder()
                                        .groupLocation(extractGroupLocation(location))
                                        .groupLocationId(extractGroupLocationId(location))
                                        .address(extractAddress(s))
                                        .imageUrl(extractImageUrl(s))
                                        .latitude(latlng.split(",")[0])
                                        .longitude(latlng.split(",")[1])
                                        .googleMapUrl(Store.toGoogleMapUrl(latlng))
                                        .build();
                            }
                    ).toList()
            );
        }
        return stores;
    }

    @SneakyThrows
    private Element retrieveHtmlFromResource() {
        return JSoupWebCrawler.readHtmlFromResourceFile(resourceFile.getFile());
    }

    private String extractGroupLocation(Element groupLocation) {
        return groupLocation.select("h2").text();
    }

    private String extractGroupLocationId(Element groupLocation) {
        return groupLocation.attr("data-state");
    }

    private String extractAddress(Element storeItem) {
        return storeItem.attr("data-fulladdress");
    }

    private String extractImageUrl(Element storeItem) {
        return storeItem.attr("data-image1");
    }

    private String extractGeographicLocation(Element storeItem) {
        return storeItem.attr("data-latlng");
    }

}
