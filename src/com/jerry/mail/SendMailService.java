package com.jerry.mail;

public interface SendMailService {
	public void sendMail(String to, String subject, String content);
}
