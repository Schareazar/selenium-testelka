package Course.OtherTests;

import Course.POMTests.BaseTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.NetworkInterceptor;
import org.openqa.selenium.devtools.v85.log.Log;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.print.PageMargin;
import org.openqa.selenium.print.PageSize;
import org.openqa.selenium.print.PrintOptions;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.http.Route;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

import static org.openqa.selenium.remote.http.Contents.utf8String;

public class CourseExtras extends BaseTests {
    private final String astronomySlug = "history-of-astronomy-by-george-forbes/";
    private final By addToWishlistLocator = By.cssSelector("a.add_to_wishlist");
    private final By removeFromWishlistLocator = By.cssSelector(".delete_item");
    private final By addToCartLocator = By.name("add-to-cart");
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void AdditionalSetup()
    {
        driver = browser.driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @Test
    public void windowSizeAndPositionManipulation()
    {
        driver.manage().window().setSize(new Dimension(1600, 900));
        driver.manage().window().setPosition(new Point(0,1));
        //driver.manage().window().fullscreen();
        driver.navigate().refresh();
        driver.navigate().to("http://localhost:8080/");
        Dimension size = driver.manage().window().getSize();
        Point position = driver.manage().window().getPosition();
        String pageSource = driver.getPageSource();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1600, size.getWidth()),
                () -> Assertions.assertEquals(900, size.getHeight()),
                () -> Assertions.assertEquals(0, position.getX()),
                () -> Assertions.assertEquals(1, position.getY()),
                () -> Assertions.assertFalse(pageSource.isEmpty())
        );
    }
    @Test
    public void fluentWait()
    {
        driver.get("http://localhost:8080/product/" + astronomySlug);
        Find(addToWishlistLocator).click();

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(7))
                .pollingEvery(Duration.ofNanos(200))
                .ignoring(NoSuchElementException.class);

        wait.until(ExpectedConditions.presenceOfElementLocated(removeFromWishlistLocator));
        driver.findElement(removeFromWishlistLocator).click();
        Assertions.assertTrue(wait.until(ExpectedConditions.presenceOfElementLocated(addToWishlistLocator)).isDisplayed());
    }
    @Test
    public void itemsInCartCookieValueUpdatesCorrectly()
    {
        driver.get("http://localhost:8080/product/" + astronomySlug);
        Find(addToCartLocator).click();
        Find(addToCartLocator).click();

        Cookie itemsInCartCookie = driver.manage().getCookieNamed("woocommerce_items_in_cart");
        int cookieNumber = driver.manage().getCookies().size();
        Cookie newCookie = new Cookie("test", "5");
        driver.manage().addCookie(newCookie);

        Assertions.assertAll(
                () -> Assertions.assertEquals(cookieNumber + 1, driver.manage().getCookies().size()),
                () -> Assertions.assertEquals("2", itemsInCartCookie.getValue())
        );
        driver.manage().deleteAllCookies();
    }
    @Test
    public void canSelectIceCreamFlavor()
    {
        driver.get("https://fakestore.testelka.pl/lista-rozwijana/");
        WebElement flavors = Find(By.cssSelector("select#flavors"));
        Select FlavorsDropDown = new Select(flavors);
        FlavorsDropDown.selectByVisibleText("marakuja");
        FlavorsDropDown.selectByValue("strawberry");
        Assertions.assertEquals("truskawkowy", FlavorsDropDown.getFirstSelectedOption().getText());
    }
    @Test
    public void printPageExample() {
        driver.get("http://localhost:3000/");
        PrintsPage printer = (PrintsPage) driver;
        PrintOptions printOptions = new PrintOptions();
        PageSize pageSize = new PageSize(27.94, 21.59);
        printOptions.setPageSize(pageSize);
        PageMargin pageMargin = new PageMargin(0, 0, 0, 0);
        printOptions.setPageMargin(pageMargin);
        printOptions.setBackground(true);
        printOptions.setScale(0.50);

        Pdf pdf = printer.print(printOptions);
        String pageContent = pdf.getContent();

        Path outputPath = Paths.get("./target/media/pageContent.pdf");

        byte[] decodedPageContent = Base64.getDecoder().decode(pageContent);
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, decodedPageContent);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while writing the PDF file: " + e);
        }
        Assertions.assertNotEquals(0, decodedPageContent.length);
    }
    @Test
    public void screenshotExample()
    {
        driver.get("http://localhost:3000/");
        WebElement element = Find(By.cssSelector(".items-end.mx-auto"));
        File pageScreenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        File elementScreenshot = element.getScreenshotAs(OutputType.FILE);
        Path pageOutputPath = Paths.get("./target/media/pageScreenshot.jpg");
        Path elementOutputPath = Paths.get("./target/media/element.jpg");
        try {
            Files.copy(pageScreenshot.toPath(), pageOutputPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(elementScreenshot.toPath(), elementOutputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Screenshot save failed" + e);
        }
        Assertions.assertTrue(pageScreenshot.exists() && elementScreenshot.exists());
    }
    @Test
    public void javascriptExample()
    {
        driver.get("http://localhost:3000/");
        JavascriptExecutor js = (JavascriptExecutor)driver;

        WebElement twitterLink = Find(By.cssSelector("a[href='https://twitter.com/ralllyco']"));
        WebElement rallyStartButton = Find(By.cssSelector("a.bg-primary-500"));

        js.executeScript("arguments[0].scrollIntoView()", twitterLink);
        String rallyStartButtonText = (String) js.executeScript("return arguments[0].innerText", rallyStartButton);

        Assertions.assertAll(
                () -> Assertions.assertTrue(twitterLink.isDisplayed()),
                () -> Assertions.assertEquals("Get started", rallyStartButtonText)
        );
    }
    @Test
    //Chromium only
    public void consoleLogsExample()
            {
                DevTools devTools = ((HasDevTools)driver).getDevTools();
                devTools.createSession();
                devTools.send(Log.enable());
                devTools.getDomains().events().addConsoleListener(
                        log -> System.out.println(
                                log.getTimestamp() + " " + log.getType() + "\n" + log.getMessages()
                        )
                );
                driver.get("https://fakestore.testelka.pl/console-log-events");
            }
    @Test
    //Chromium only
    public void jsExceptionsExample()
    {
        DevTools devTools = ((HasDevTools)driver).getDevTools();
        devTools.createSession();
        devTools.getDomains().events().addJavascriptExceptionListener(System.out::println);
        driver.get("https://fakestore.testelka.pl/javascript-exceptions");
        Find(By.id("button-1")).click();
    }
    @Test
    public void interceptAndChangeResponseExample()
    {
        NetworkInterceptor networkInterceptor = new NetworkInterceptor(driver,
                Route.matching(req -> req.getUri().contains("store"))
                        .to(() -> req -> new HttpResponse().setStatus(200)
                        .setHeader("Content-type", "text/css")
                        .setContent(utf8String("Test"))));
        driver.get("https://fakestore.testelka.pl");
        networkInterceptor.close();
    }
    @Test
    public void basicAuthExample()
    {
        ((HasAuthentication)driver).register(UsernameAndPassword.of("harrypotter", "Alohomora"));
        driver.get("https://fakestore.testelka.pl/wp-content/uploads/protected/cos.html");
    }
    @Test
    public void alertExample()
    {
        driver.get("https://fakestore.testelka.pl/alerty/");
        Find(By.cssSelector("[onclick='confirmFunction()']")).click();
        Alert alert = driver.switchTo().alert();
        Assertions.assertEquals("Wciśnij coś:", alert.getText());
        alert.accept();
        Assertions.assertEquals("Wybrana opcja to OK!", Find(By.cssSelector("#demo")).getText());
        Find(By.className("woocommerce-store-notice__dismiss-link")).click();
        Find(By.cssSelector("[onclick='delayedAlertFunction()']")).click();
        Alert delayedAlert = wait.until(ExpectedConditions.alertIsPresent());
        Assertions.assertEquals("Miałem mały poślizg", delayedAlert.getText());
    }
    @Test
    public void multipleWindowsExample()
    {
        driver.get("http://localhost:8080/product/" + astronomySlug);
        String firstTab = driver.getWindowHandle();
        String secondTab = driver.switchTo().newWindow(WindowType.TAB).getWindowHandle();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        driver.get("http://localhost:8080/");
        driver.switchTo().window(firstTab);
        driver.switchTo().newWindow(WindowType.TAB).getWindowHandle();
        driver.close();
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(firstTab, secondTab),
                () -> Assertions.assertEquals(2, driver.getWindowHandles().size())
        );
    }
    @Test
    public void ctrlClickExample()
    {
        driver.get("https://fakestore.testelka.pl/select/");
        List<WebElement> items = FindMany(By.cssSelector(".ui-selectee"));
        Actions actions = new Actions(driver);
        actions
                .keyDown(Keys.CONTROL)
                .click(items.get(1))
                .click(items.get(2))
                .click(items.get(3))
                .doubleClick(items.get(4))
                .keyUp(Keys.CONTROL)
                .perform();
        List<WebElement> selectedItems = FindMany(By.cssSelector(".ui-selected"));
        Assertions.assertEquals(3, selectedItems.size(), "3 items are not selected");
    }
    @Test
    public void dragAndDropExample()
    {
        driver.get("https://fakestore.testelka.pl/actions/");
        Actions actions = new Actions(driver);
        WebElement draggable = Find(By.cssSelector("#draggable"));
        WebElement droppable = Find(By.cssSelector("#droppable"));
        actions
                .dragAndDrop(draggable,droppable)
                .perform();
        actions
                .clickAndHold(draggable)
                .moveToElement(droppable, 75, 75)
                .release()
                .perform();

        Assertions.assertEquals("Dropped!", droppable.getText(), "Item wasn't moved correctly");
    }
}
