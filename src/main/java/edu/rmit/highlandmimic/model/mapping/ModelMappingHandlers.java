package edu.rmit.highlandmimic.model.mapping;

import edu.rmit.highlandmimic.model.*;

import java.util.*;

public class ModelMappingHandlers {

    public static List<ProductCatalogue> convertListOfIdsToCatalogues(List<ProductCatalogue> catalogues, List<String> catalogueIds) {
        return Optional.ofNullable(catalogueIds)
                .map(ids -> ids.stream().map(
                            idString -> catalogues.stream()
                                    .filter(catalogueDocument -> catalogueDocument.getProductCatalogueId().equals(idString))
                                    .findFirst()
                                    .orElseThrow(() -> new NoSuchElementException("There is no catalogue with id: " + idString + " in product-catalogues collection"))
                        ).toList()
                ).orElse(new ArrayList<>());
    }

    public static List<Topping> convertListOfIdsToToppings(List<Topping> toppings, List<String> toppingIds) {
        return Optional.ofNullable(toppingIds)
               .map(ids -> ids.stream().map(
                            idString -> toppings.stream()
                                   .filter(toppingDocument -> toppingDocument.getToppingId().equals(idString))
                                   .findFirst()
                                   .orElseThrow(() -> new NoSuchElementException("There is no topping with id: " + idString + " in toppings collection"))
                        ).toList()
                ).orElse(new ArrayList<>());
    }

    public static List<Tag> convertListOfIdsToTags(List<Tag> tags, List<String> tagIds) {
        return Optional.ofNullable(tagIds)
                .map(ids -> ids.stream().map(
                                idString -> tags.stream()
                                        .filter(toppingDocument -> toppingDocument.getTagId().equals(idString))
                                        .findFirst()
                                        .orElseThrow(() -> new NoSuchElementException("There is no topping with id: " + idString + " in toppings collection"))
                        ).toList()
                ).orElse(new ArrayList<>());
    }

    public static List<Product> convertListOfIdsToProducts(List<Product> referenceProducts, List<String> productIds) {
        return Optional.ofNullable(productIds)
              .map(ids -> ids.stream().map(
                        idString -> referenceProducts.stream()
                              .filter(productDocument -> productDocument.getProductId().equals(idString))
                              .findFirst()
                              .orElseThrow(() -> new NoSuchElementException("There is no product with id: " + idString + " in products collection"))
                        ).toList()
                ).orElse(new ArrayList<>());
    }

    public static List<Coupon> convertListOfIdsToCoupons(List<Coupon> referenceCoupons, List<String> coupons) {
        return Optional.ofNullable(coupons)
              .map(ids -> ids.stream().map(
                        idString -> referenceCoupons.stream()
                              .filter(couponDocument -> couponDocument.getId().equals(idString))
                              .findFirst()
                              .orElseThrow(() -> new NoSuchElementException("There is no coupon with id: " + idString + " in coupons collection"))
                        ).toList()
                ).orElse(new ArrayList<>());
    }
}
