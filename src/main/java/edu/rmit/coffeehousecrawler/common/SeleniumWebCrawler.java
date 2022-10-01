package edu.rmit.coffeehousecrawler.common;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumWebCrawler {

    private WebDriver driver;
    private String url;

    public SeleniumWebCrawler(String url) {

        WebDriverManager.chromedriver().setup();

        this.driver = new ChromeDriver();
        this.url = url;
    }

    public String getHtmlSource() {
        this.driver.get(this.url);
        this.driver.quit();
        return "";
    }


}
