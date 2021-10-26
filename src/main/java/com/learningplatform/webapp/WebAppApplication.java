package com.learningplatform.webapp;

import com.learningplatform.webapp.security.SpringApplicationContext;
import com.learningplatform.webapp.security.properties.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class WebAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebAppApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SpringApplicationContext springApplicationContext() {
		return new SpringApplicationContext();
	}

	@Bean(name = "AppProperties")
	public AppProperties getAppProperties(){
		return new AppProperties();
	}
}
