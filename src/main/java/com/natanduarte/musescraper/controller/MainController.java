package com.natanduarte.musescraper.controller;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Value("${url.taylor}")
    private String url;

    private static final int WINDOWS = 8;

    @PostConstruct
    public void init() {
        List<WebDriver> drivers = new ArrayList<>();
        WebDriver driver = null;
        for (int i = 0; i < WINDOWS; i++) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(
                    new FirefoxOptions().addArguments(
//                        "--headless",
                            "--no-sandbox",
                            "--ignore-certificate-errors",
                            "--disable-dev-shm-usage",
                            "--disable-gpu",
                            "--disable-extensions",
                            "--incognito"
                    )
            );

            drivers.add(driver);

            driver.manage().window().maximize();

            try {
                driver.get(url);

                FluentWait<WebDriver> fluentWait = new FluentWait<>(driver);

                WebElement button = fluentWait.withTimeout(Duration.ofSeconds(10))
                        .pollingEvery(Duration.ofMillis(500))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.link-block-12:nth-child(3)")));

                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new java.util.Scanner(System.in).nextLine();

        for (WebDriver webDriver : drivers)
            if (webDriver != null) webDriver.quit();
    }
}
