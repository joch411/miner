package fr.joch.miner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class MiningTest {

	private WebElement velocity;
	private WebElement shares;
	private int indexToMine = 1;
	private WebElement lastShare;

	@Test
	public void testMining() throws Exception {
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("cred")));

		if (System.getProperty("indexToMine") != null) {
			indexToMine = Integer.parseInt(System.getProperty("indexToMine"));
		}

		WebDriver webDriver;
		if ("chrome".equals(System.getProperty("driver"))) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("no-sandbox");
			options.addArguments("whitelisted-ips=\"\"");
			webDriver = new ChromeDriver(options);
		}
		else {
			webDriver = new FirefoxDriver();
		}
		webDriver.manage().window().maximize();
		Cookie token = new Cookie("token",
				"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJqb2NoNDFAZ21haWwuY29tIiwiaWF0IjoxNDgxNDA2MzAxLCJleHAiOjE0OTY5NTgzMDF9.sCxxeeray8XxEM4c7XMAzKc-w30oXqve2nS4JLM6N7k",
				".minergate.com", "/",
				new SimpleDateFormat("dd/MM/yyyy").parse("08/06/2017"), false,
				false);
		webDriver.get("https://fr.minergate.com/web-miner");
		webDriver.manage().addCookie(token);

		Thread.sleep(2000);
		webDriver.get("https://fr.minergate.com/internal");
		Thread.sleep(5000);
		for (WebElement e : webDriver
				.findElements(By.className("mine-title"))) {
			System.out.println(e.getText().replace("\r\n", "")
					.replace("BTC", "").replace("Changelly", "").replace(
							"Historique de retraitHistorique des transfertsDébiterEnvoyer",
							""));
		}
		webDriver.get("https://fr.minergate.com/web-miner");

		System.out.println("\n\n\n\n\n\n-------------------------------");
		System.out.println("Mining on : " + webDriver
				.findElements(By.className("web-miner")).get(indexToMine)
				.findElement(By.className("name")).getText());

		if (!webDriver.getPageSource().contains("Se déconnecter")) {
			System.err.println("ERROR COOKIE");
			System.exit(1);
		}
		restartMining(webDriver);

		int last0 = 0;
		for (int i = 0; i < 40 * 60 / 2; i++) {
			float v = extractVelocity();
			System.out.println(MessageFormat.format("{0}H/s| {1} shares - Last : {2}",
					Float.toString(v), getShares(), getLastShare()));
			if (v == 0) {
				last0++;
			}
			else {
				last0 = 0;
			}
			if (last0 == 25 || getLastShare().equals("il y a 5 minutes")) {
				restartMining(webDriver);
				last0 = 0;
				Thread.sleep(2000);
			}
			Thread.sleep(2000);
		}

		WebElement monero = webDriver.findElements(By.className("web-miner"))
				.get(indexToMine);
		monero.findElement(By.className("btn")).click();
		velocity = monero.findElements(By.className("data")).get(0);
		shares = monero.findElements(By.className("data")).get(1)
				.findElement(By.tagName("b"));
	}

	private void restartMining(WebDriver webDriver) throws Exception {
		WebElement monero = webDriver.findElements(By.className("web-miner"))
				.get(indexToMine);
		if ("Stop".equals(monero.findElement(By.className("btn")).getText())) {
			System.out.println("Restarting mining ....");
			monero.findElement(By.className("btn")).click();
			Thread.sleep(2000);
		}
		monero.findElement(By.className("btn")).click();
		Thread.sleep(5000);

		velocity = monero.findElements(By.className("data")).get(0);
		shares = monero.findElements(By.className("data")).get(1)
				.findElement(By.tagName("b"));
		lastShare = monero.findElements(By.className("data")).get(1)
				.findElement(By.tagName("small"))
				.findElements(By.tagName("span")).get(1);
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

	private String getLastShare() {
		String text = lastShare.getText();
		return text;
	}
}
