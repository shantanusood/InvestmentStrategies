package com.invest.objects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Objects {

	public WebElement priceEle(WebDriver driver) {
		
		WebElement priceEle = driver.findElement(By.id("quoteContainer"));
		
		List<WebElement> allDivUnder = priceEle.findElements(By.tagName("div"));
		
		return allDivUnder.get(1);
		
	}
	

	public WebElement dataTable(WebDriver driver) {
	
		return driver.findElement(By.className("dataTable"));
	}

	public List<WebElement> performanceTable(WebDriver driver) {
	
		return driver.findElements(By.id("table-trailingTotalReturnsTable"));
		
	}	
	
	public boolean waitForElement(WebDriver driver, By by) throws InterruptedException {
		boolean hasAppeared = false;
		
		long timeAfter = 0;
		long timeElapsed;
		long timeBefore = System.currentTimeMillis();
		
		List<WebElement> ele_list = driver.findElements(by);
		
		while(ele_list.size()==0) {
			Thread.sleep(250);
			ele_list = driver.findElements(by);
			timeAfter = System.currentTimeMillis();
			timeElapsed = timeAfter - timeBefore;
			System.out.println("Waiting, time elepased: "+timeElapsed);
			if(ele_list.size()>0) {
				hasAppeared = true;
				break;
			}
			if(timeElapsed>10000) {
				break;
			}
		}
		
		return hasAppeared;
	}
	
}
