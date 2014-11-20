package com.abid.akka_email.email.service;

import javax.inject.Named;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

@Named("EmailService")
public class EmailService {

	@Autowired
	private JavaMailSenderImpl mailSender;

	public void sendMail(String from, String to, String subject, String message)
			throws MessagingException {

		try {
			mailSender.send(getMimeMessage(from, to, subject, message));
		} catch (AddressException ex) {
			new MessagingException("Address is invalid", ex);
		} catch (MessagingException ex) {
			throw ex;
		}

	}

	private MimeMessage getMimeMessage(String from, String to, String subject,
			String message) throws AddressException, MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(message, true);
		return mimeMessage;
	}

}
