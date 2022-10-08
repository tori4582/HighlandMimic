package edu.rmit.coffeehousecrawler.repository;

import edu.rmit.coffeehousecrawler.common.JSoupWebCrawler;
import edu.rmit.coffeehousecrawler.common.SeleniumWebCrawler;
import edu.rmit.coffeehousecrawler.model.Post;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
public class PostRepository {

    public List<Post> getPostsOnPage(String url, Integer pageNumber) {
        Element pageBody = JSoupWebCrawler.fetchBodyHtmlContentFromUrl(
                url + ((pageNumber == 1) ? "" : ("?" + "page=" + pageNumber))
        );
        return pageBody.select("div.blog_item").stream().skip(1).map(e -> {
            return Post.builder()
                    .postUrl(extractRefUrl(e))
                    .title(extractTitle(e))
                    .imageUrl(extractImageUrl(e))
                    .page(pageNumber)
                    .collectionName(pageBody.getElementsByTag("h1").first().text())
                    .uploadedDuration(extractUploadedTime(e))
                    .shortenedContent(extractShortenedContent(e))
                    .articleContent(extractArticleContent(extractRefUrl(e)))
                    .build();
        }).toList();
    }

    public List<Post> getPostsOfCollection(String collectionUrl) {
        Element pageBody = JSoupWebCrawler.fetchBodyHtmlContentFromUrl(collectionUrl);
        int maxPage = extractMaxPage(pageBody);
        return IntStream.range(1, maxPage).mapToObj(i -> this.getPostsOnPage(collectionUrl, i))
                .reduce(new ArrayList<>(), (acc, post) -> { acc.addAll(post); return acc; });
    }

    private String extractRefUrl(Element postItemElement) {
        return Optional.ofNullable(postItemElement.select("h3>a").first())
                .map(e -> e.attr("href"))
                .orElse(null);
    }

    private String extractTitle(Element postItemElement) {
        return Optional.ofNullable(postItemElement.select("h3>a").first())
               .map(Element::text)
               .orElse(null);
    }

    private String extractImageUrl(Element postItemElement) {
        return Optional.ofNullable(postItemElement.select("div.article_img").first())
               .map(e -> e.attr("style")
                       .substring(23)
                       .replace(")", "")
               ).orElse(null);
    }

    private String extractUploadedTime(Element postItemElement) {
        return Optional.ofNullable(postItemElement.select("time").first())
              .map(Element::text)
              .orElse(null);
    }

    private String extractShortenedContent(Element postItemElement) {
        return Optional.ofNullable(postItemElement.select("div.short_des").first())
                .map(Element::text)
                .orElse(null);
    }

    private int extractMaxPage(Element pageBody) {
        List<Element> pageButtons = pageBody.select("ul.pagination>li>a");
        return Integer.parseInt(pageButtons.get(pageButtons.size() - 2).text()) + 1;
    }

    private String extractArticleContent(String refUrl) {
        return JSoupWebCrawler.fetchBodyHtmlContentFromUrl("http://thecoffeehouse.com" + refUrl)
                .select("div.article_content")
                .html();
    }
}
