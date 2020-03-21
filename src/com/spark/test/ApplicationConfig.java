package com.spark.test;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

    @Value("${app.name}")
    private String appName;

	/*
	 * @Value("${spark.home}") private String sparkHome;
	 */
    @Bean
    public SparkConf sparkConf() {
        SparkConf sparkConf = new SparkConf()
                .set("spark.app.name" ,appName)
                //.set("spark.master", "spark://192.168.1.157:7077");
                .set("spark.master", "local[*]");
        return sparkConf;
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .config(sparkConf())
                .getOrCreate();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}