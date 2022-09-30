package edu.rmit.coffeehousecrawler;

import edu.rmit.coffeehousecrawler.common.JSoupWebCrawler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CoffeehouseCrawlerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getContentFromUrl() {
        log.info(JSoupWebCrawler.fetchBodyHtmlContentFromUrl("https://thecoffeehouse.com/collections/all").toString());
    }

}
