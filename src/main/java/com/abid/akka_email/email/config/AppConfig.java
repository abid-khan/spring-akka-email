package com.abid.akka_email.email.config;

import static com.abid.akka_email.email.config.SpringExtension.SpringExtProvider;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import akka.actor.ActorSystem;

@Configuration
@ComponentScan(basePackages = { "com.abid.akka_email.email" })
@PropertySource(value = "classpath:application.properties")
public class AppConfig {

	@Autowired
	private Environment environment;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "mailSender")
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(environment.getProperty("javax.mail.host"));
		mailSender.setPort(Integer.parseInt(environment
				.getProperty("javax.mail.port")));
		mailSender.setUsername(environment.getProperty("javax.mail.username"));
		mailSender.setPassword(environment.getProperty("javax.mail.password"));
		mailSender.setProtocol(environment.getProperty("javax.mail.protocol"));
		mailSender.setDefaultEncoding(environment
				.getProperty("javax.mail.encoding"));

		Properties javaMailProperties = new Properties();
		javaMailProperties.setProperty("mail.smtp.auth",
				environment.getProperty("javax.mail.smtp.auth"));
		javaMailProperties.setProperty("mail.smtp.starttls.enable",
				environment.getProperty("javax.mail.smtp.starttls.enable"));

		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

	@Bean
	public ClassLoaderTemplateResolver emailTemaplateResolver() {

		ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("email-templates/");
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setOrder(1);
		return templateResolver;

	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(emailTemaplateResolver());
		return templateEngine;
	}

	@Bean(name = "EmailActorSystem")
	public ActorSystem actorSystem() {
		ActorSystem emailSystem = ActorSystem.create("emailSystem");
		SpringExtProvider.get(emailSystem).initialize(applicationContext);
		return emailSystem;
	}

}
