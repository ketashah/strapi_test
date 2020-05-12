

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class StrapiTest {

	private static WebDriver driver;

	// Declare UI elements
	static WebElement blockContentTypes;
	static WebElement blockPlugins;
	static WebElement ctbMenu;
	static WebElement rolesPermMenu;
	static WebElement contentMgrMenu;

	WebElement btnGetStarted;
	WebElement btnHelpMeFind;
	WebElement inputBedsideTableCounter;

	@BeforeClass
	public static void Initialize() throws InterruptedException {

		// Environment Variable PATH refers to this directory. No need to add it every
		// time
		System.setProperty("webdriver.chrome.driver", "C://Selenium//chromedriver.exe");

		// Ensure that selenium-server-standalone-3.141.59.jar is added in Build Path
		// Libraries
		driver = new ChromeDriver();

		if (driver == null) {
			System.out.println("Driver not initialized");
			return;
		}

		// implicit wait
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

		// Open home page of MakeSpace - 1
		driver.get("http://localhost:1337/admin");

		// Maximize browser
		driver.manage().window().maximize();

		// authorize local user
		(driver.findElement(By.id("identifier"))).sendKeys("kshah");
		(driver.findElement(By.id("password"))).sendKeys("keta123");
		(driver.findElement(By.xpath("//button[@type='submit']"))).click();

		// verify basic plug-ins installation 
		ctbMenu = driver.findElement(By.xpath("//a[@href='/admin/plugins/content-type-builder']"));
		rolesPermMenu = driver.findElement(By.xpath("//a[@href='/admin/plugins/users-permissions']"));
		contentMgrMenu = driver.findElement(By.xpath("//a[@href='/admin/plugins/content-manager/ctm-configurations']"));

		boolean basicPluginsFound = ctbMenu != null && rolesPermMenu != null && contentMgrMenu != null;

		assertTrue(
				"Basic plugins (Content Type Builder, Roles & Permissions and Content Manager) should be installed to proceed",
				basicPluginsFound);
		System.out.println("Required basic plugins are installed");
	}

	@AfterClass
	public static void CleanUp() throws InterruptedException {

		Thread.sleep(2000);
		if(driver != null) { 
			driver.close(); 
		}
		
	}

	@Before
	public void SetUp() {

	}

	@Test
	public void TestContentTypeBuilder() throws InterruptedException {

		String typeName = "Article";
		
		ctbMenu.click(); 
		WebElement btnAddContentType = driver.findElement(By.id("openAddCT")); 
		btnAddContentType.click();
		
		WebElement divModal = driver.findElement(By.className("modal-content"));
		(divModal.findElement(By.id("name"))).sendKeys(typeName);
		(divModal.findElement(By.id("description"))).sendKeys("Sample Articles");
		(divModal.findElement(By.xpath(".//button[@type='submit']"))).click();
		System.out.println("Content Type created");
		
		Thread.sleep(1000);
		
		divModal = driver.findElement(By.className("modal-content"));
		(divModal.findElement(By.id("attrCardstring"))).click();
		System.out.println("Adding String attribute"); Thread.sleep(1000);
		
		divModal = driver.findElement(By.className("modal-content"));
		(divModal.findElement(By.id("name"))).sendKeys("Title");
		(divModal.findElement(By.xpath(".//div/span[text()='Advanced settings']"))).
		click(); (divModal.findElement(By.id("required"))).click();
		(divModal.findElement(By.id("unique"))).click();
		(divModal.findElement(By.xpath(".//button/span[text()='Save']"))).click();
		Thread.sleep(1000);
		
		
		(driver.findElement(By.id("saveData"))).click();
		
		Thread.sleep(3000);
		
		// At this point, Server Restart happens to persist new content type.
		// Restart is taking longer than expected. Breaking further testing.
		
		// Ensure category exists
		ctbMenu.click();
		WebElement ctbModelsList = driver.findElement(By.id("ctbModelsList"));
		WebElement articleModel = ctbModelsList.findElement(By.xpath(".//span[text()[contains(., '" + typeName + "')]]"));
		assertTrue(String.format("{0} content type should be saved", typeName) ,  articleModel != null);
		
		System.out.println("Everything looks good!");
		
		
	}

}

