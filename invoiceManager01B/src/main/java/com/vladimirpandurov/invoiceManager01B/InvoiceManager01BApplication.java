package com.vladimirpandurov.invoiceManager01B;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication//(exclude = {SecurityAutoConfiguration.class})
public class InvoiceManager01BApplication {
	private static final int STRENGHT = 12;

	public static void main(String[] args) {
		SpringApplication.run(InvoiceManager01BApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder(STRENGHT);
	}

}
