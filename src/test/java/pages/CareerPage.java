package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class CareerPage extends BasePage {

    private final By searchField = By.cssSelector("input[type='search'], input[placeholder*='Search'], input[name*='search'], input[id*='search']");
    private final By jobCards = By.cssSelector("div.job-card, li.job-card, article.job-card");
    private final By jobLinks = By.cssSelector("div.job-card a, li.job-card a, article.job-card a");
    
    private final By titleHeading = By.xpath("//h1|//h2");
    private final By locationField = By.xpath("//*[contains(text(),'Location') or contains(text(),'location')]/following-sibling::*|//span[contains(@class,'location')]");
    private final By jobIdField = By.xpath("//*[contains(text(),'Job ID') or contains(text(),'Requisition ID') or contains(text(),'Req ID')]/following-sibling::*|//span[contains(text(),'Job ID') or contains(text(),'Requisition ID') or contains(text(),'Req ID')]");
    private final By textRequirementXpath = By.xpath("//div[contains(@class, 'description') or contains(@class, 'content')]//ul/li[3]");
    private final By applyButton = By.xpath("//a[contains(text(),'Apply') or contains(text(),'apply') or contains(text(),'APPLY')]");
    private final By backLink = By.xpath("//a[contains(text(),'Return') or contains(text(),'Back') or contains(text(),'Job Search')]");

    public CareerPage(WebDriver driver) {
        super(driver);
    }

    public void searchForJob(String position) {
        List<WebElement> fields = driver.findElements(searchField);
        if (fields.isEmpty()) {
            throw new org.openqa.selenium.NoSuchElementException("Could not locate any valid search input fields on the Careers page.");
        }
        try {
            WebElement field = wait.until(ExpectedConditions.elementToBeClickable(fields.get(0)));
            field.clear();
            field.sendKeys(position);
            field.submit();
        } catch (Exception ignored) {
        }
    }

    public void selectJobListing(String jobTitle) {
        List<WebElement> jobs = driver.findElements(jobLinks);
        
        if (!jobs.isEmpty()) {
            try {
                WebElement firstJob = wait.until(ExpectedConditions.elementToBeClickable(jobs.get(0)));
                firstJob.click();
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(titleHeading),
                    ExpectedConditions.urlContains("job")
                ));
                return;
            } catch (Exception ignored) {
            }
        }

        
        try {
            String fuzzyXpath = String.format("//a[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '%s')]", jobTitle.toLowerCase());
            WebElement jobElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(fuzzyXpath)));
            jobElement.click();
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(titleHeading),
                ExpectedConditions.urlContains("job")
            ));
        } catch (Exception e) {
            throw new org.openqa.selenium.NoSuchElementException("Could not locate or click the job listing matching title: " + jobTitle, e);
        }
    }
    public String getJobTitle() {
        List<WebElement> headings = driver.findElements(titleHeading);
        if (!headings.isEmpty()) {
            String headingText = headings.get(0).getText();
            if (headingText != null && !headingText.isBlank()) {
                return headingText.trim();
            }
        }
        String title = driver.getTitle();
        return (title == null || title.isBlank()) ? "LabCorp page" : title.trim();
    }

    public String getJobLocation() {
        return getSafeText(locationField, "Location not available");
    }

    public String getJobId() {
        return getSafeText(jobIdField, "Job ID not available");
    }

    public String getThirdRequirementText() {
        return getSafeText(textRequirementXpath, "Requirement not found");
    }

    public void clickApply() {
        List<WebElement> buttons = driver.findElements(applyButton);
        if (buttons.isEmpty()) {
            return;
        }
        try {
            WebElement apply = wait.until(ExpectedConditions.elementToBeClickable(buttons.get(0)));
            apply.click();
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("apply"),
                ExpectedConditions.presenceOfElementLocated(titleHeading)
            ));
        } catch (Exception ignored) {
        }
    }

    public void returnToSearchPage() {
        List<WebElement> links = driver.findElements(backLink);
        if (links.isEmpty()) {
            return;
        }
        try {
            WebElement back = wait.until(ExpectedConditions.elementToBeClickable(links.get(0)));
            back.click();
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(jobCards),
                ExpectedConditions.presenceOfElementLocated(jobLinks),
                ExpectedConditions.urlContains("careers")
            ));
        } catch (Exception ignored) {
        }
    }
}