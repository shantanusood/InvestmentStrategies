package com.invest.java;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.invest.objects.Objects;

public class CommonFunctions {

	public static List<String> getETFList(Workbook workbook){
		
		List<String> etfList = new ArrayList<String>();
		
		Sheet sheet = workbook.getSheetAt(0);
		
		int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		
		for(int i=1;i<rowCount + 1;i++) {
				
			etfList.add(sheet.getRow(i).getCell(2).toString());
		}
	
		System.out.println(etfList);
		return etfList;
	}
	
	public static List<String> getData(WebDriver driver, String etf) throws IOException, InterruptedException{

		Objects ob = new Objects();
		
		Properties prop = new Properties();
		FileInputStream readPropFile = null;
		readPropFile = new FileInputStream("Const.properties");
		prop.load(readPropFile);
		
		List<String> dataList = new ArrayList<String>();
		
		try {
			String ameriTradeLink = prop.getProperty("TOPURL") + etf;
			
			driver.get(ameriTradeLink);
			
			ob.waitForElement(driver, By.id("SmartChartContainer"));
			
			List<WebElement> labels = ob.dataTable(driver).findElements(By.className("fleft"));
			List<WebElement> data = ob.dataTable(driver).findElements(By.className("fright"));
			
			//Get etf price
			String etfPrice = ob.priceEle(driver).getText().trim();
			
			StringBuilder sb_p = new StringBuilder(etfPrice);
			sb_p.deleteCharAt(0);
			dataList.add(sb_p.toString());
			
			System.err.println("*******************");
			System.err.println(etf+" : "+etfPrice);
			
			for(int i=0;i<labels.size();i++) {
				
				if(labels.get(i).getText().contains("Gross Expense Ratio")) {
	
					String expenseRatio = data.get(i).getText().trim();
					StringBuilder sb = new StringBuilder(expenseRatio);
					sb.deleteCharAt(expenseRatio.length()-1);
					dataList.add(sb.toString());
					
					
				}else if(labels.get(i).getText().contains("Inception")) {
					
					String inception = data.get(i).getText().trim();
					dataList.add(inception);
					
				}else if(labels.get(i).getText().contains("Total Assets")) {
	
					String assets = data.get(i).getText().trim();
					StringBuilder sb = new StringBuilder(assets);
					sb.deleteCharAt(0);
					sb.deleteCharAt(assets.length()-2);
					
					String totalAssets = "";
					
					if(assets.contains("B")) {
						totalAssets = "000000000";
					}else if(assets.contains("M")) {
						totalAssets = "000000";
					}
					
					dataList.add(sb.toString()+totalAssets);
					
				}else if(labels.get(i).getText().contains("Distribution Yield")) {
					
					String yield = data.get(i).getText().trim();
					StringBuilder sb = new StringBuilder(yield);
					sb.deleteCharAt(yield.length()-1);
					dataList.add(sb.toString());
					
				}else {
	
					System.out.println(labels.get(i).getText() + " : " + data.get(i).getText());
					
				}
			}
			
			Thread.sleep(1000);
	
			String ameriTradeLinkPerf = prop.getProperty("TOPURLPERF") + etf;
			
			driver.get(ameriTradeLinkPerf);
			ob.waitForElement(driver, By.cssSelector("img[id='chart']"));
			
			List<WebElement> perfTrEle = ob.performanceTable(driver).get(0).findElements(By.tagName("tr"));
			
			for(int j=0;j<perfTrEle.size();j++) {
				
				if(perfTrEle.get(j).getText().contains("Year-to-date")) {
					String ytd = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(ytd);
					sb.deleteCharAt(0);
					sb.deleteCharAt(ytd.length()-2);
					dataList.add(sb.toString());
					
				}else if(perfTrEle.get(j).getText().contains("1-Year")) {
					String oneYear = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(oneYear);
					sb.deleteCharAt(0);
					sb.deleteCharAt(oneYear.length()-2);
					dataList.add(sb.toString());
					
				}else if(perfTrEle.get(j).getText().contains("3-Year")) {
					String threeYear = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(threeYear);
					sb.deleteCharAt(0);
					sb.deleteCharAt(threeYear.length()-2);
					dataList.add(sb.toString());
					
				}else if(perfTrEle.get(j).getText().contains("5-Year")) {
					String fiveYear = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(fiveYear);
					sb.deleteCharAt(0);
					sb.deleteCharAt(fiveYear.length()-2);
					dataList.add(sb.toString());
					
				}else if(perfTrEle.get(j).getText().contains("10-Year")) {
					String tenYear = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(tenYear);
					sb.deleteCharAt(0);
					sb.deleteCharAt(tenYear.length()-2);
					dataList.add(sb.toString());
					
				}else if(perfTrEle.get(j).getText().contains("Inception")) {
					String inception = perfTrEle.get(j).findElements(By.tagName("td")).get(1).getText().trim();
					StringBuilder sb = new StringBuilder(inception);
					sb.deleteCharAt(0);
					sb.deleteCharAt(inception.length()-2);
					dataList.add(sb.toString());
					
				}
				
			}
		}catch(Exception e) {
			
			System.err.println("Error occured, skipping " + etf + " etf");
			
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			dataList.add("Err");
			
		}
		return dataList;
	
	}
	
	public static void writeData(List<String> dataList, Workbook workbook, String fileName, String etf) {
		
		Sheet sheet = workbook.getSheetAt(0);
		
		int etfRowNum = findRow(sheet, etf);
		
		Row row2 = sheet.getRow(etfRowNum);
		
		for(int i=0;i<dataList.size();i++) {
			
			Cell cell = row2.createCell(i+9);
			cell.setCellValue(dataList.get(i));
			
		}
	}
	
	public static int findRow(Sheet sheet, String cellContent) {
		
		//System.out.println("Looking for row in file");
		for(Row row : sheet) {
			
			for(Cell cell : row) {
				if(cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if(cell.getRichStringCellValue().getString().trim().equals(cellContent)) {
						return row.getRowNum();
					}
				}
			}
		}
		
		return 0;
	}
}
