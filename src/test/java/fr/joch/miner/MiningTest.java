package fr.joch.miner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Base64;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

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
		for (int i = 0; i < 10000; i++) {
			float v = extractVelocity();
			System.out.println(MessageFormat.format("{0}H/s| {1} shares", v, getShares()));
			if (v == 0) {
				last0++;
			}
			if (last0 == 5) {
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
		velocity = monero.findElements(By.className("data")).get(1);
	}

	private float extractVelocity() {
		String text = velocity.getText().replace("Taux de hachage", "");
		text = text.replace("\r\n", "").replace("<br>", "").replace("H/s", "");

		return Float.parseFloat(text);
	}
	
	private long getShares() {
		String text = shares.getText().replace("Taux de hachage", "");
		text = text.replace("\r\n", "").replace("<br>", "").replace("H/s", "");

		return Long.parseLong(text);
	}
}
