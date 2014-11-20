package com.abid.akka_email.email.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.springframework.context.annotation.Scope;

import akka.actor.UntypedActor;

import com.abid.akka_email.email.message.EmailMessage;

@Named("EmailWorkerActor")
@Scope("prototype")
public class EmailWorkerActor extends UntypedActor {

	private EmailService emailService;

	@Inject
	public EmailWorkerActor(@Named("EmailService") EmailService emailService) {
		this.emailService = emailService;
	}

	@Override
	public void onReceive(Object message) {
		try {
			EmailMessage emailMessage = (EmailMessage) message;
			emailService.sendMail(emailMessage.getFrom(), emailMessage.getTo(),
					emailMessage.getSubject(), emailMessage.getMessage());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void preStart() {

	}

	@Override
	public void postStop() {

	}
}
