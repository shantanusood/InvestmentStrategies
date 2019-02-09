package com.invest.scripts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.invest.java.CommonFunctions;

public class scripts {

	public static WebDriver driver = null;
	
	@BeforeSuite
	public static void startUp() {
		
		driver = new ChromeDriver();
	}
	
	@Test
	public static void reportingChanges() throws IOException, InvalidFormatException, InterruptedException {
		
		Properties prop = new Properties();
		FileInputStream readPropFile = null;
		readPropFile = new FileInputStream("Const.properties");
		prop.load(readPropFile);
		
		String fileName = prop.getProperty("ETFDATASHEETPATH") + prop.getProperty("ETFDATASHEETNAME");
		
		FileInputStream inputStream = new FileInputStream(fileName);
		Workbook workbook = WorkbookFactory.create(inputStream);
	
		//Get ETF List
		List<String> etfList = CommonFunctions.getETFList(workbook);
		
		//Get Data for particular etf
		for(int i=0;i<etfList.size();i++) {
			
			//Get Data
			List<String> dataList = CommonFunctions.getData(driver, etfList.get(i));
			
			//Write Data to excel
			CommonFunctions.writeData(dataList, workbook, fileName, etfList.get(i));
		
		}
				
		inputStream.close();
		
		FileOutputStream outputStream = new FileOutputStream(fileName);
		workbook.write(outputStream);
		
		outputStream.close();
		
	}
	
	@AfterTest
	public static void end() {
		
		driver.close();
	}
	
}
