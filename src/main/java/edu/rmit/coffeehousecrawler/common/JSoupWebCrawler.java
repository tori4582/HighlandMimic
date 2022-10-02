package edu.rmit.coffeehousecrawler.common;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;

@Slf4j
public class JSoupWebCrawler {

    @SneakyThrows
    public static Element fetchBodyHtmlContentFromUrl(String url) {
        return Jsoup.parse(fetchRequestAsChromeClient(url).body().html())
                .getElementsByTag("body")
                .first();
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
