package Course.OtherTests;
import Course.POMTests.BaseTests;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class AdminPanelTests extends BaseTests {

    private final String newProducts = "post-new.php?post_type=product";
    private final String editProducts = "edit.php?post_type=product";
    private final String adminLink = "http://localhost:8080/wp-admin/";
    private final String uploadMedia = "upload.php";

    @BeforeEach
    public void adminLogin()
    {
        WebDriver driver = browser.driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get("http://localhost:8080/my-account/");
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin");
        driver.findElement(By.name("login")).click();
    }

    @Test
    public void successfulAdminLoginDisplaysAccountContent()
    {
        Assertions.assertDoesNotThrow(() -> browser.driver.findElement(By.className("woocommerce-MyAccount-content")),
                "Login failed");
    }
    @Test
    public void virtualProductShouldNotBeShippable()
    {
        browser.driver.get(adminLink + newProducts);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("_virtual"))).click();
        WebElement shipping = browser.driver.findElement(By.className("shipping_options"));
        Assertions.assertFalse(shipping.isDisplayed());
    }
    @Test
    public void selectAllShouldTickAllBoxes()
    {
        browser.driver.get(adminLink + editProducts);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("activity-panel-tab-setup")));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("cb-select-all-1"))).click();
        List<WebElement> productCheckboxes = browser.driver.findElements(By.name("post[]"));
        // Testelka solution
//        Assertions.assertEquals(productCheckboxes.size(), productCheckboxes.stream().filter
//         (checkbox -> checkbox.isSelected()).count(), "Not all checkboxes are ticked");
        // My solution
        for (WebElement checkbox: productCheckboxes
        ) {
            Assertions.assertTrue(checkbox.isSelected());
        }
    }

    @Test
    public void newMediaCanBeAdded()
    {
        WebDriver driver = browser.driver;
        driver.get(adminLink + uploadMedia);
        Find(By.cssSelector("a.page-title-action")).click();
        Find(By.cssSelector("input[type=file]")).sendKeys("C:\\Users\\kukaw\\Desktop\\capture.jpg");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[aria-label=\"capture\"]"))).click();

        Assertions.assertEquals("File size: 212 B", Find(By.cssSelector(".file-size")).getText());

        Find(By.className("delete-attachment")).click();
        driver.switchTo().alert().accept();
    }
}
