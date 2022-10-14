package main.java.de.voidtech.alison.service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Geolocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowserUtils {

	private static final Logger LOGGER = Logger.getLogger(BrowserUtils.class.getSimpleName());
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64; Trident/7.0; rv:11.0) like Gecko";
	private static BrowserContext browser = null;
	
	private static NewContextOptions getContextOptions() {
		return new Browser.NewContextOptions()
				.setViewportSize(1000, 1000)
				.setAcceptDownloads(false)
				.setGeolocation(new Geolocation(51.5072, 0.1276))
				.setUserAgent(USER_AGENT)
				.setLocale("en-GB");
	}
	
	public static void initialise() {
		ExecutorService playwrightExecutor = ThreadManager.getThreadByName("Playwright");
		playwrightExecutor.execute(() -> {
            LOGGER.log(Level.INFO, "Playwright is being initialised");
            Browser browserInstance = Playwright.create().firefox().launch();
            browser = browserInstance.newContext(getContextOptions());
            LOGGER.log(Level.INFO, "Playwright is ready!");
        });
	}
	
	public static BrowserContext getBrowser() {
		return browser;
	}
	
	public static byte[] searchAndScreenshot(String url) {
		Page screenshotPage = getBrowser().newPage();
		screenshotPage.setExtraHTTPHeaders(getHttpHeaders());
		screenshotPage.navigate(url); 
		screenshotPage.setViewportSize(1000, 1000);
		byte[] screenshotBytesBuffer = screenshotPage.screenshot();
		screenshotPage.close();	
		
		return screenshotBytesBuffer;
	}

	private static Map<String, String> getHttpHeaders() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Language", "en");
		return headers;
	}

}