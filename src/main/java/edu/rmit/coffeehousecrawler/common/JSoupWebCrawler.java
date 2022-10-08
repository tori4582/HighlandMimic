package edu.rmit.coffeehousecrawler.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.Optional;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JSoupWebCrawler {

    @SneakyThrows
    public static Element fetchBodyHtmlContentFromUrl(String url) {
        return fetchHtml(url).body();
    }

    @SneakyThrows
    public static Element fetchHeadHtmlContentFromUrl(String url) {
        return fetchHtml(url).head();
    }

    public static Document fetchHtml(String url) {
        log.info("Establishing connection to URL: " + url);
        return fetchRequestAsChromeClient(url);
    }

    @SneakyThrows
    public static String extractMetadata(Element headElement, String property) {
        return Optional.ofNullable(
                        headElement.select("meta[property=" + property + "]").first()
                )
                .map(e -> e.attr("content"))
                .orElse("");
    }

    @SneakyThrows
    public static Document readHtmlFromResourceFile(File file) {
        return Jsoup.parse(file);
    }

    @SneakyThrows
    private static Document fetchRequestAsChromeClient(String url) {
//        return Jsoup.connect(url)
//                    .userAgent("Chrome")
//                    .timeout(10000)
//                    .method(Connection.Method.GET)
//                    .followRedirects(true)
//                    .execute();
        return Jsoup.connect(url).get();
    }


}
