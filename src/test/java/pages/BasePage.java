package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    protected void clickLink(String linkText) {
        WebElement element;
        try {
            String xpath = String.format("//a[descendant-or-self::*[contains(text(), '%s')] or contains(., '%s')]", linkText, linkText);
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        } catch (Exception e) {
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText)));
        }

        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].click();", element);
        }
    }

    protected void enterTextById(String id, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        element.clear();
        element.sendKeys(text);
    }

    protected String getTextByXpath(String xpath) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        return element.getText().trim();
    }

    // Integrated safe multi-element parsing strategy from working LabCorp reference code
    protected String getSafeText(By locator, String defaultValue) {
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty()) {
            String text = elements.get(0).getText();
            if (text != null && !text.isBlank()) {
                return text.trim();
            }
        }
        return defaultValue;
    }
}