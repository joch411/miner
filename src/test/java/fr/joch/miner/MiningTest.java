package fr.joch.miner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class MiningTest {

	private WebElement velocity;
	private WebElement shares;
	private int indexToMine = 3;
	private WebElement lastShare;

	@Test
	public void testMining() throws Exception {
		if (System.getProperty("indexToMine") != null) {
			indexToMine = Integer.parseInt(System.getProperty("indexToMine"));
		}

		WebDriver webDriver;
		if ("chrome".equals(System.getProperty("driver"))) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--no-sandbox");
			options.addArguments("whitelisted-ips=\"\"");
			webDriver = new ChromeDriver(options);
		}
		else {
			unzipProfile(new File(".").getAbsolutePath());
			FirefoxProfile profile = new FirefoxProfile(
					new File(new File("").getAbsolutePath() + File.separator
							+ "wbph3zp0.miner"));
			System.out.println(new File("").getAbsolutePath() + File.separator + "wbph3zp0.miner");
			FirefoxBinary binary = new FirefoxBinary();
			binary.setTimeout(180000);
			webDriver = new FirefoxDriver(binary, profile);
		}
		
		System.out.println("Started");
		webDriver.manage().window().maximize();
		webDriver.get("https://fr.minergate.com/internal");
		webDriver.findElement(By.className("logo-wrapper")).findElement(By.tagName("a")).click();
		Thread.sleep(3000);
		webDriver.findElement(By.className("iconed-dashboard")).click();
		Thread.sleep(3000);
		webDriver.findElement(By.className("iconed-mining")).click();
		Thread.sleep(3000);
		webDriver.findElement(By.className("iconed-dashboard")).click();
		
		Thread.sleep(5000);
		for (WebElement e : webDriver
				.findElements(By.className("mine-title"))) {
			System.out.println(e.getText().replace("\r\n", "")
					.replace("BTC", "").replace("Changelly", "").replace(
							"Historique de retraitHistorique des transfertsDébiterEnvoyer",
							""));
		}
		webDriver.findElement(By.className("subheader")).findElements(By.tagName("a")).get(4).click();
		Thread.sleep(3000);
		webDriver.findElement(By.name("infoForm")).findElement(By.className("btn")).click();
		Thread.sleep(3000);
		webDriver.findElement(By.className("iconed-mining")).click();

		System.out.println("\n\n\n\n\n\n-------------------------------");
		System.out.println("Mining on : " + webDriver
				.findElements(By.className("web-miner")).get(indexToMine)
				.findElement(By.className("name")).getText());

		Thread.sleep(5000);
		if (!webDriver.getPageSource().contains("Se déconnecter")
				|| webDriver.getPageSource()
						.contains("To mine real money, please authorize")) {
			System.err.println("ERROR LOGIN");
			System.exit(1);
		}
		restartMining(webDriver);

		int last0 = 0;
		int min = 40;
		if (System.getProperty("timeout") != null) {
			min = Integer.parseInt(System.getProperty("timeout"));
		}
		for (int i = 0; i < min * 60; i++) {
			float v = extractVelocity();
			System.out.println(
					MessageFormat.format("{0}H/s| {1} shares - Last : {2}",
							Float.toString(v), getShares(), getLastShare()));
			if (v == 0) {
				last0++;
			}
			else {
				last0 = 0;
			}
			if (last0 == 20) {
				restartMining(webDriver);
				last0 = 0;
				Thread.sleep(2000);
			}
			if (getLastShare().equals("il y a 10 minutes")) {
				WebElement monero = webDriver.findElements(By.className("web-miner"))
						.get(indexToMine);
				monero.findElement(By.className("btn")).click();
				Thread.sleep(2000);
				webDriver.findElement(By.className("iconed-mining")).click();
				Thread.sleep(2000);
				restartMining(webDriver);
				Thread.sleep(2000);
			}
			Thread.sleep(1000);
		}

		WebElement monero = webDriver.findElements(By.className("web-miner"))
				.get(indexToMine);
		monero.findElement(By.className("btn")).click();
		velocity = monero.findElements(By.className("data")).get(0);
		shares = monero.findElements(By.className("data")).get(1)
				.findElement(By.tagName("b"));
	}

	private void unzipProfile(String base) throws Exception {
		ZipFile zipFile = new ZipFile(
				getClass().getResource("wbph3zp0.miner.profile").getFile());
		Enumeration<?> enu = zipFile.entries();
		while (enu.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) enu.nextElement();
			String name = zipEntry.getName();

			File file = new File(base + File.separator + name);
			if (name.endsWith("/")) {
				file.mkdirs();
				continue;
			}

			File parent = file.getParentFile();
			if (parent != null) {
				parent.mkdirs();
			}

			// Extract the file
			InputStream is = zipFile.getInputStream(zipEntry);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = is.read(bytes)) >= 0) {
				fos.write(bytes, 0, length);
			}
			is.close();
			fos.close();

		}
		zipFile.close();
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
