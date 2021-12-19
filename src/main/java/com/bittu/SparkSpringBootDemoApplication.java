package com.bittu;

import org.apache.spark.sql.SparkSession;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SparkSpringBootDemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SparkSpringBootDemoApplication.class, args);
	}

	@Override

	public void run(String... args) throws Exception {

		SparkSession sparkSession = SparkSession

				.builder()

				.appName("SparkWithSpring")

				.master("local")

				.getOrCreate();

		System.out.println("Spark Version: " + sparkSession.version());

	}









}
