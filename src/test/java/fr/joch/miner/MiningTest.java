package fr.joch.miner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class MiningTest {

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

		WebElement monero = webDriver.findElements(By.className("web-miner"))
				.get(1);
		monero.findElement(By.className("btn")).click();
		for (int i = 0; i < 100; i++) {
			System.out.println(
					monero.findElements(By.className("data")).get(0).getText());
			Thread.sleep(2000);
		}

		Thread.sleep(10000);
	}
}
