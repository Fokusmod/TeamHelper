package ru.geekbrains.WowVendorTeamHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


@SpringBootApplication
@ServletComponentScan
public class WowVendorTeamHelperApplication {


	public static void main(String[] args) {
		SpringApplication.run(WowVendorTeamHelperApplication.class, args);
	}




}
