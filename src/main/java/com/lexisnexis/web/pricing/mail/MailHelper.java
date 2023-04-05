package com.lexisnexis.web.pricing.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailHelper {
	
	public static void sendMail(String jiraId, String jiraDesc) {

		Session session = Session.getInstance(MailConfig.getMailProperties(), new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(MailConfig.getProperty("user"), MailConfig.getProperty("password"));
			}
		});
		session.setDebug(true);

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(MailConfig.getProperty("user")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(MailConfig.getProperty("toList")));
			message.setSubject("Price Testing Results for " + jiraId + " " + jiraDesc);
			message.setText("Please find attached the automated Price Testing results for the PRS attached to "
					+ jiraId + " " + jiraDesc + "." + "The status of the run is COMPLETED.");

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
}
