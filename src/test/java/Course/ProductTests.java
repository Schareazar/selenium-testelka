package Course;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ProductTests extends BaseTests{

    private final By addToCartLocator = By.name("add-to-cart");
    private final String addToCartLocator2 = "button[name='add-to-cart']";
    private final By updateCartLocator = By.name("update_cart");
    protected String astronomySlug = "history-of-astronomy-by-george-forbes/";
    @Test
    public void updatingCartShouldBeDisabledIfQuantityIsUnchanged()
    {

        driver.get(baseUrl + "/product/" + astronomySlug);
        driver.findElement(addToCartLocator).click();
        driver.get("http://localhost:8080/cart/");
        WebElement updateButton = driver.findElement(updateCartLocator);
        Assertions.assertFalse(updateButton.isEnabled(),
                "Cart update button was enabled even though quantity wasn't changed");
    }
    @Test
    public void updatingQuantityShouldChangeTotalPrice()
    {
        driver.get(baseUrl + "/product/" + astronomySlug);
        driver.findElement(addToCartLocator).click();
        driver.get("http://localhost:8080/cart/");
        String totalBefore = driver.findElement(By.className("order-total")).getText();
        WebElement quantityField = driver.findElement(By.className("qty"));
        quantityField.clear();
        quantityField.sendKeys("2");
        driver.findElement(updateCartLocator).click();
        //wait.until(ExpectedConditions.numberOfElementsToBe(By.className("blockUI"), 0));
        wait.until(driver -> driver.findElements(By.className("blockUI")).size() == 0);
        WebElement totalAfter = driver.findElement(By.className("order-total"));
        Assertions.assertNotEquals(totalBefore ,totalAfter.getText(),
                "Total wasn't changed after cart update");
    }
    @Test
    public void alternativeUpdatingQuantityShouldChangeTotalPrice()
    {
        driver.get(baseUrl + "/product/" + astronomySlug);
        driver.findElement(addToCartLocator).click();
        driver.get("http://localhost:8080/cart/");
        String totalBefore = driver.findElement(By.className("order-total")).findElement(By.className("amount")).getText();
        WebElement quantityField = driver.findElement(By.className("qty"));
        quantityField.clear();
        quantityField.sendKeys("2");
        driver.findElement(updateCartLocator).click();
        //wait.until(ExpectedConditions.numberOfElementsToBe(By.className("blockUI"), 0));
        wait.until(driver -> driver.findElements(By.className("blockUI")).size() == 0);
        WebElement totalAfter = driver.findElement(By.className("order-total")).findElement(By.className("amount"));
        Assertions.assertNotEquals(totalBefore ,totalAfter.getText(),
                "Total wasn't changed after cart update");
    }
    @Test
    public void addingToCartShouldUpdateProductCounter()
    {
        ProductPage productPage = new ProductPage(driver);
        productPage.open(astronomySlug);
        productPage.click(addToCartLocator2);
        productPage.waitToAppear(".wc-block-mini-cart__badge");
        Assertions.assertEquals("1", (productPage.getText(".wc-block-mini-cart__badge")),
                "Number of products in the cart wasn't updated from 0 to 1");
    }
    @Test
    public void addingToCartShouldUpdateTotalInCart()
    {
        driver.get(baseUrl + "/product/" + astronomySlug);
        driver.findElement(By.name("add-to-cart")).click();
        driver.findElement(By.className("wc-block-mini-cart__button")).click();
        WebElement totalPrice = wait.until(driver -> driver.findElement(By.className("wc-block-components-totals-item__value")));
        Assertions.assertEquals("20,00 €", totalPrice.getText(),"Total was not updated correctly");

    }
    @Test
    public void defaultProductQuantityShouldBe1()
    {
        driver.get(baseUrl + "/product/" + astronomySlug);
        WebElement defaultQuantity = driver.findElement(By.className("qty"));
        Assertions.assertEquals("1",  defaultQuantity.getDomProperty("defaultValue"),
                "Default value is different than 1");
    }
}