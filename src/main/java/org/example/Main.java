package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.devtools.DevTools;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Set;

public class Main {
    private WebDriver driver;
    private WebDriverWait wait;
    private DevTools devTools;

    @BeforeClass
    public void setUp(){
        WebDriverManager.chromedriver().setup();
        Path extensionPath = Paths.get("Extension", "Pline.crx").toAbsolutePath();
        System.out.println("Extension Path: " + extensionPath.toString());
        File extensionFile = new File(extensionPath.toString());
        if (!extensionFile.exists()) {
            throw new IllegalArgumentException("Extension file does not exist: " + extensionPath.toString());
        }
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(extensionFile);
     //   options.setExperimentalOption("w3c", false);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();


    }
    @Test
    public void testPlineExtensionUI() {
        driver.get("https://www.daraz.com.np/");
        String originalWindow = driver.getWindowHandle();
        assert driver.getWindowHandles().size() == 1;
        Set<String> windowHandles = driver.getWindowHandles();
        System.out.println("Window handles before popup: " + windowHandles);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String mainWindow = driver.getWindowHandle();
        String popupWindow = wait.until(driver -> {
            Set<String> handles = driver.getWindowHandles();
            handles.removeAll(windowHandles);
            return handles.size() > 0 ? handles.iterator().next() : null;
        });

        driver.switchTo().window(popupWindow);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'ant-flex') and contains(@class, 'css-1ng2uih') ]\n")));
        String elementText = element.getText();
        Assert.assertEquals(elementText, "Get Started", "The text does not match!");
        element.click();
        WebElement someElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("")));
        someElement.click();

        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'ant-btn') and contains(@class, 'css-1ng2uih') and contains(@class, 'ant-btn-primary')]")));
        String buttonText = button.getText();
        Assert.assertEquals(buttonText, "Build Workflow", "The button text does not match!");


        driver.close();
        driver.switchTo().window(mainWindow);

    }

    @AfterClass
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}