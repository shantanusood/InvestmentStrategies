package com.spark.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class Main {

	/*
	 * private static final String CSV_SEPARATOR = ",";
	 * 
	 * @Autowired private SparkSession sparkSession;
	 */	
	public String main() throws IOException {
		
		SparkConf conf = new SparkConf().set("spark.master", "local[*]");
		SparkSession spark = SparkSession.builder().config(conf).appName("PrimeApp").getOrCreate();
		
		List<String> rawData = readData().subList(0, 50);
		
		List<Row> dataR = new ArrayList<>();
		for(String st : rawData) {
		    dataR.add(RowFactory.create(st));
		}
		
		StructType schema = new StructType(new StructField[] { 
                new StructField("st", DataTypes.StringType, false, Metadata.empty())
        });
		
		Dataset<Row> data = spark.createDataFrame(dataR, schema);
		Dataset<OutputData> out = data.map(new mainTest(), Encoders.bean(OutputData.class));
		
		return formatIt(new Gson().toJson(out.toDF().collect()));
		
	}
	
	public String formatIt(String unformatter) throws JsonParseException, JsonMappingException, IOException {
		
		Gson gson = new Gson();
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jn = objectMapper.readTree(unformatter);
		Map<String, ArrayList<String>> dataMap = new HashMap<>();

		JsonNode fields = jn.get(0).get("schema").get("fields");
		dataMap.put("ticker", new ArrayList<String>());
		
		for(int j=0;j<fields.size();j++) {
			if(!fields.get(j).get("name").asText().equals("ticker"))
				dataMap.get("ticker").add(fields.get(j).get("name").asText());
		}
		
		for(int i=0;i<jn.size();i++) {
			ArrayList<String> lt = new ArrayList<String>();
			
			JsonNode j = jn.get(i).get("values");
			for(int k=0;k<j.size();k++) {
				lt.add(j.get(k).asText());
			}
			dataMap.put(lt.get(lt.size()-1), new ArrayList<String>(lt.subList(0, lt.size()-1)));
		}
		return new Gson().toJson(dataMap);
	}
	

	//For reading from local file system
	public List<String> readData() throws IOException { 
	    String file = "C:\\Users\\shant\\OneDrive\\Desktop\\Ticker.csv";
	    List<String> content = new ArrayList<>();
	    try(BufferedReader br = new BufferedReader(new FileReader(file))) {
	        String line = "";
	        while ((line = br.readLine()) != null) {
	            content.add(line.replaceFirst(",", ""));
	        }
	    } catch (FileNotFoundException e) {
	      e.printStackTrace();
	    }
	    content.remove(0);
	    return content;
	}
	
}

class mainTest implements MapFunction<Row, OutputData>{

	@Override
	public OutputData call(Row ticker) throws Exception {

		OutputData outData = new OutputData();
		outData.setTicker(ticker.get(0).toString());
		try {
			Document doc = Jsoup.connect("https://research.tdameritrade.com/grid/public/etfs/profile/profile.asp?symbol="+ticker.get(0).toString()).get();
			Elements ele_etfName = doc.body().select("h2[id=\"companyName\"]");
			Elements ele_etfPrice = doc.body().select("div[class*=\"primaryQuote\"] > div[class=\"fLeft\"]");
			Elements ele_etfExp = doc.body().select("div[class=\"dataTable\"] > div:nth-child(1)[class*=\"hasLayout\"] > div[class=\"fright\"]");
			Elements ele_etfYield = doc.body().select("div[class=\"dataTable\"] > div:nth-child(5)[class*=\"hasLayout\"] > span[class=\"fright\"]");
			Elements ele_etfOneMonth = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(1) > td:nth-child(3) ");
			Elements ele_etfThreeMonth = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(2) > td:nth-child(3) ");
			Elements ele_etfSixMonth = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(3) > td:nth-child(3) ");
			Elements ele_etfYtd = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(4) > td:nth-child(3) ");
			Elements ele_etfOneYear = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(5) > td:nth-child(3) ");
			Elements ele_etfThreeYear = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(6) > td:nth-child(3) ");
			Elements ele_etfFiveYear = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(7) > td:nth-child(3) ");
			Elements ele_etfTenYear = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(8) > td:nth-child(3) ");
			Elements ele_etfInception = doc.body().select("div[id=\"module-trailingTotalReturns\"] >table[id=\"table-trailingTotalReturnsTable\"] > tbody > tr:nth-child(9) > td:nth-child(3) ");

			outData.setStr_etfName(ele_etfName.text().trim());
			outData.setStr_etfPrice(ele_etfPrice.text().trim());
			outData.setStr_etfExp(ele_etfExp.text().trim());
			outData.setStr_etfYield(ele_etfYield.text().trim());
			outData.setStr_etfOneMonth(ele_etfOneMonth.text().trim());
			outData.setStr_etfThreeMonth(ele_etfThreeMonth.text().trim());
			outData.setStr_etfSixMonth(ele_etfSixMonth.text().trim());
			outData.setStr_etfYtd(ele_etfYtd.text().trim());
			outData.setStr_etfOneYear(ele_etfOneYear.text().trim());
			outData.setStr_etfThreeYear(ele_etfThreeYear.text().trim());
			outData.setStr_etfFiveYear(ele_etfFiveYear.text().trim());
			outData.setStr_etfTenYear(ele_etfTenYear.text().trim());
			outData.setStr_etfInception(ele_etfInception.text().trim());
			
		}catch(Exception e) {
			
		}
		
		return outData;
	
	}
	
}