package edu.rmit.coffeehousecrawler.repository;

import edu.rmit.coffeehousecrawler.common.JSoupWebCrawler;
import edu.rmit.coffeehousecrawler.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Repository
public class ProductRepository {

    private static final String BASE_URL = "https://thecoffeehouse.com";
    private static final String COLLECTIONS_PATH = "/collections/all";

    private static final String SELECTOR_COLLECTION = "div.cha";
    private static final String SELECTOR_PRODUCT = "div.menu_item";

    private static final String SELECTOR_REF_LINK = "h3>a";

    public List<Product> fetchAllProducts() {

        var collections = this.extractCollections(
                JSoupWebCrawler.fetchBodyHtmlContentFromUrl(BASE_URL + COLLECTIONS_PATH)
        );

        return collections.stream().map(this::extractProductsFromCollection)
                .map(products -> products.stream()
                        .map(this::toProduct)
                        .collect(Collectors.toList())
                )
                .reduce(new ArrayList<>(), (arr, list) -> {
                    arr.addAll(list);
                    return arr;
                });
    }

    public Product toProduct(Element productElement) {

        String productUrl = BASE_URL + productElement.select(SELECTOR_REF_LINK).attr("href");

        Document productPage = JSoupWebCrawler.fetchHtml(productUrl);

        Element productPageBody = productPage.body();
        Element productPageHead = productPage.head();

//        log.info("Fetched product html page: \n" + productElement.html());

        return Product.builder()
                .name(extractTitleFromProductHead(productPageHead))
                .productUrl(productUrl)
                .price(this.extractPriceFromProductHead(productPageHead))
                .currency(this.extractCurrencyFromProductHead(productPageHead))
                .collectionName(
                        this.extractCollectionFromBreadcrumb(productPageBody.select("ol.breadcrumb").first())
                )
                .imageUrl(extractImageUrlFromProductHead(productPageHead))
                .description(this.extractDescriptionFromProductBody(productPageBody))
                .build();

    }

    private String extractCurrencyFromProductHead(Element headElement) {
        return JSoupWebCrawler.extractMetadata(headElement, "og:price:currency");
    }

    private List<Element> extractCollections(Element bodyElement) {
        return new ArrayList<>(bodyElement.select(SELECTOR_COLLECTION));
    }

    private List<Element> extractProductsFromCollection(Element collectionElement) {
        return new ArrayList<>(collectionElement.select(SELECTOR_PRODUCT));
    }


    private String extractTitleFromProductHead(Element headElement) {

        return JSoupWebCrawler.extractMetadata(headElement, "og:title");
    }

    private String extractCollectionFromBreadcrumb(Element breadcrumbElement) {
        return Optional.ofNullable(breadcrumbElement)
                .map(e -> breadcrumbElement.select("li>a>span")
                        .get(1)
                        .text()
                ).orElse("");
    }

    private Long extractPriceFromProductHead(Element headElement) {

        String priceNumber =  Optional.ofNullable(
                        headElement.select("meta[property=og:price:amount]").first()
                )
                .map(e -> e.attr("content"))
                .map(s -> s.split(" ")[0].replace(".", ""))
                .orElse("");

        return (priceNumber.length() == 0) ? 0L : Long.parseLong(priceNumber);
    }

    private String extractImageUrlFromProductHead(Element headElement) {

        return JSoupWebCrawler.extractMetadata(headElement, "og:image");
    }

    private String extractDescriptionFromProductBody(Element productPageBody) {
        return Stream.of(
            productPageBody.select("div.product_content_bottom")
                    .select("div")
                    .get(0)
                    .select("p")
        ).map(Elements::text)
        .reduce("", (acc, p) -> acc + p);
    }

}
