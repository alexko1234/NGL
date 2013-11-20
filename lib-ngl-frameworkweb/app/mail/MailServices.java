package mail;

import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import com.typesafe.config.ConfigFactory;

import play.Logger;

public class MailServices {

	private static final String mailSmtpHost = ConfigFactory.load().getString("mailSmtpHost");
	
	public void sendMail(String from, Set<String> to, String subject, String message) throws MailServiceException {

		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", mailSmtpHost);
		Session session = Session.getInstance(properties, null);

		try {
			Message msg = new MimeMessage(session);
			
			msg.setFrom(new InternetAddress(from));
			for(String mail : to){
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mail));
			}
			msg.setSubject(subject);
			msg.setContent(message, "text/html");
			Transport.send(msg);
			Logger.debug("Mail sent to : " + to);

		} catch (Exception e) {
			Logger.debug("Mail NOT sent => " + e.getMessage());
			throw new MailServiceException(e);
		}
	}
}
