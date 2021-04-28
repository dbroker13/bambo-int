package appium.seetest_specific.performance;

import com.experitest.appium.SeeTestClient;
//import helpers.PropertiesReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.testng.annotations.*;
import org.openqa.selenium.By;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class PerformanceTransaction {

    /**
     *
     * ==================================================
     *                      READ ME                     =
     * ==================================================
     *
     * This approach is ALSO applicable for AndroidDriver.
     *
     * In SeeTestCloud, we have the ability to capture Performance Metrics for our Mobile Tests.
     * In the Report, we capture data such as Average & Maximum consumed CPU / Memory / Battery, as well as
     * Network Traffic, Speed Index, and if applicable, download / upload speed for the Network Profile applied.
     *
     * https://docs.experitest.com/display/TE/StartPerformanceTransactionForApplication
     * https://docs.experitest.com/display/TE/EndPerformanceTransaction
     *
     * https://docs.experitest.com/display/TE/Transaction+report
     * https://docs.experitest.com/display/TE/Transaction+View
     *
     */

    protected IOSDriver<IOSElement> driver = null;
    protected DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    SeeTestClient client;

    @BeforeMethod
    public void setUp(Method method) throws MalformedURLException, FileNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(System.getProperty("user.home") + "//.seetest.properties");
        prop.load(input);

        desiredCapabilities.setCapability("testName", method.getName());
        desiredCapabilities.setCapability("accessKey", prop.getProperty("accessKey"));
        desiredCapabilities.setCapability("deviceQuery", "@os='ios' and @category='PHONE'");
        //desiredCapabilities.setCapability("deviceQuery", "@os='ios' and @emulator='true'");
        desiredCapabilities.setCapability(MobileCapabilityType.APP, "cloud:com.experitest.ExperiBank");
        desiredCapabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, "com.experitest.ExperiBank");
        driver = new IOSDriver<>(new URL(prop.getProperty("cloud.url")), desiredCapabilities);
        client = new SeeTestClient(driver);
    }

    @Test
    public void performance_transaction_testing() {
        driver.rotate(ScreenOrientation.PORTRAIT);

        // Start of a Transaction
        driver.executeScript("seetest:client.startPerformanceTransactionForApplication(\"com.experitest.ExperiBank\", \"4G-Lossy\")");

        driver.findElement(By.id("usernameTextField")).sendKeys("company");
        driver.findElement(By.id("passwordTextField")).sendKeys("company");
        driver.findElement(By.id("loginButton")).click();

        // End  of a Transaction
        Object loginTransaction = driver.executeScript("seetest:client.endPerformanceTransaction(\"Login_Transaction\")");
        System.out.println(loginTransaction.toString());

        // Start of a Transaction
        driver.executeScript("seetest:client.startPerformanceTransactionForApplication(\"com.experitest.ExperiBank\", \"3G-Lossy\")");
        driver.findElement(By.id("makePaymentButton")).click();
        driver.findElement(By.id("phoneTextField")).sendKeys("0541234567");
        driver.findElement(By.id("nameTextField")).sendKeys("Jon Snow");
        driver.findElement(By.id("amountTextField")).sendKeys("50");
        driver.findElement(By.id("countryButton")).click();
        driver.findElement(By.id("Switzerland")).click();
        driver.findElement(By.id("sendPaymentButton")).click();
        driver.findElement(By.id("Yes")).click();

        // End  of a Transaction
        Object paymentTransaction = driver.executeScript("seetest:client.endPerformanceTransaction(\"Payment_Transaction\")");
        System.out.println(paymentTransaction.toString());
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }
}
