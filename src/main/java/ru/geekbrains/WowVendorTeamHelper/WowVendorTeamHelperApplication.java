package ru.geekbrains.WowVendorTeamHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@ServletComponentScan
public class WowVendorTeamHelperApplication {


	public static void main(String[] args) {
		SpringApplication.run(WowVendorTeamHelperApplication.class, args);
	}




}
