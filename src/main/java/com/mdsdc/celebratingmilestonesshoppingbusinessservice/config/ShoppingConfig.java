package com.mdsdc.celebratingmilestonesshoppingbusinessservice.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class ShoppingConfig{
	@Bean
	public JavaMailSender getJavaSender() {
		JavaMailSenderImpl mailSender= new JavaMailSenderImpl();
		mailSender.setHost("smtp.gmail.com");
		mailSender.setPort(587);
		mailSender.setUsername("ms.celebratingmilestones@gmail.com");
		mailSender.setPassword("sygyhoyciuvxffdz");
		Properties properties= new Properties();
		properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.smtp.ssl.enable","true");
        properties.setProperty("mail.test-connection","true");
        properties.setProperty("mail.smtp.socketFactory.fallback", "true");
		mailSender.setJavaMailProperties(properties);
		return mailSender;
	}
}
