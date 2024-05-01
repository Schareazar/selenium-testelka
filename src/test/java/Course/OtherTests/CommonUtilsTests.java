package Course.OtherTests;
import Course.POMTests.BaseTests;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import java.util.List;

public class CommonUtilsTests extends BaseTests {
    @Test
    public void urlTest()
    {
        browser.driver.get("http://localhost:8080/");
        Assertions.assertAll(
                () -> Assertions.assertEquals("http://localhost:8080/", browser.driver.getCurrentUrl(), "Url different than expected")
        );
    }
    @Test
    public void firstFindBy()
    {
        WebDriver driver = browser.driver;
        driver.get("http://localhost:8080/");
        WebElement searchField = driver.findElement(By.id("wc-block-search__input-1"));
        searchField.click();
        List<WebElement> addToCart = driver.findElements(By.className("add_to_cart_button"));
        addToCart.clear();
    }
    @Test
    public void searchBoxShouldHavePlaceholder()
    {
        browser.driver.get("http://localhost:8080/");
        WebElement searchBox = browser.driver.findElement(By.id("wc-block-search__input-1"));
        Assertions.assertEquals("Search products…", searchBox.getDomAttribute("placeholder"),
                "SearchBox placeholder text is incorrect");
    }

}
