package com.example.borrowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.example.commonservice","com.example.borrowservice"})
public class BorrowserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BorrowserviceApplication.class, args);
	}

}
