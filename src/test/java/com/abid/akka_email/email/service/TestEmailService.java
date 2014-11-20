package com.abid.akka_email.email.service;

import static com.abid.akka_email.email.config.SpringExtension.SpringExtProvider;

import java.util.Locale;

import javax.inject.Named;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.SmallestMailboxPool;

import com.abid.akka_email.email.config.AppConfig;
import com.abid.akka_email.email.message.EmailMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class }, loader = AnnotationConfigContextLoader.class)
public class TestEmailService {

	@Autowired
	@Named("EmailActorSystem")
	private ActorSystem emailSystem;

	@Autowired
	private TemplateEngine templateEngine;

	@Autowired
	private Environment environment;

	private EmailMessage emailMessage;

	@Before
	public void setUp() {
		final Context ctx = new Context(Locale.US);
		final String message = this.templateEngine.process(
				"email-welcome.html", ctx);

		emailMessage = new EmailMessage(
				environment.getProperty("javax.mail.from"),
				environment.getProperty("javax.mail.to"),
				environment.getProperty("javax.mail.subject"), message);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void send() {

		ActorRef emailActorRef = emailSystem.actorOf(
				SpringExtProvider.get(emailSystem).props("EmailActor")
						.withRouter(new SmallestMailboxPool(50)), "EmailActor");

		emailActorRef.tell(emailMessage, emailActorRef);
		emailSystem.awaitTermination();
	}
}
