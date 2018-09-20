package se.lth.base.server.mail;

import org.simplejavamail.MailException;

public class TestMail {

	public static void main(String[] args) {
		MailHandler mailHandler = new MailHandler();	
		CustomEmail welcomeEmail = new CustomEmail("Jon", "Stålhammar", "jost95@gmail.com", CustomEmail.WELCOME_EMAIL);
		
		try {
			mailHandler.sendMail(welcomeEmail.getEmail());
		} catch (MailException e) {
			System.out.println(e.getMessage());
		}
	}

}