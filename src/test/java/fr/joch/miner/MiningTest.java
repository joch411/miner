package fr.joch.miner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class MiningTest {

	private WebElement velocity;
	private WebElement shares;

	@Test
	public void testMining() throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("cred")));
		
		WebDriver webDriver = new FirefoxDriver();
		webDriver.manage().window().maximize();
		webDriver.get("https://fr.minergate.com/login");
		webDriver.findElement(By.name("email"))
				.sendKeys(Base64.getDecoder().decode(br.readLine()).toString());
		webDriver.findElement(By.name("password"))
				.sendKeys(Base64.getDecoder().decode(br.readLine()).toString());
		webDriver.findElement(By.cssSelector("[type='submit']")).click();

		webDriver.get("https://fr.minergate.com/web-miner");

		restartMining(webDriver);

		int last0 = 0;
		for (int i = 0; i < 3500; i++) {
			float v = extractVelocity();
			System.out.println(MessageFormat.format("{0}H/s| {1} shares",
					Float.toString(v), getShares()));
			if (v == 0) {
				last0++;
			}
			if (last0 == 8) {
				restartMining(webDriver);
				last0 = 0;
				Thread.sleep(2000);
			}
			Thread.sleep(2000);
		}
	}

	private void restartMining(WebDriver webDriver) {
		System.out.println("Restarting mining ....");
		WebElement monero = webDriver.findElements(By.className("web-miner"))
				.get(1);
		monero.findElement(By.className("btn")).click();

		velocity = monero.findElements(By.className("data")).get(0);
		shares = monero.findElements(By.className("data")).get(1)
				.findElement(By.tagName("b"));
	}

	private float extractVelocity() {
		String text = velocity.getText().replace("Taux de hachage", "");
		text = text.replace("\r\n", "").replace("<br>", "").replace("H/s", "");

		return Float.parseFloat(text);
	}

	private String getShares() {
		String text = shares.getText().replace("Partages envoyer.", "");
		text = text.replace("\r", "").replace("\n", "").replace("<br>", "")
				.replace("H/s", "");
		return text;
	}
}
