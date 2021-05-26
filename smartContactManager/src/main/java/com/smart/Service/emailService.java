package com.smart.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class emailService {

	public boolean sendEmail(String subject,String message,String to)
	{
		//rest of the code
		
		boolean f=false;
		
		String from="my.name.d987@gmail.com";
		
		//variable for gmail
		String host="smtp.gmail.com";
		
		
		Properties properties = System.getProperties();
		System.out.println("PROPERTIES"+properties);
		
		//setting important info in property object
		
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//step 1:- to get the session object
		
		Session session = Session.getInstance(properties,new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new  PasswordAuthentication(  "my.name.d987@gmail.com", "Deva@12345678");
			}
			
			
			
			
		});
		
		//step 2 compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);
		try {
			
			//from email
			m.setFrom(from);
			
			//addding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject
			m.setSubject(subject);
			
			//adding text to message
			//m.setText(message);
			
			m.setContent(message,"text/html");
			
			
			//send
			
			//step 3 send the message using transport class
			Transport.send(m);
			
			System.out.println("sent success............");
			f=true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
		
	}
	
	
	
}
