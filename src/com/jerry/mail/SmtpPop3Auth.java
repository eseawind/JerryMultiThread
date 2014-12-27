package com.jerry.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SmtpPop3Auth extends Authenticator {
	public String user;
	public String password;

	// 设置帐号信息
	public void setAccount(String user, String password) {
		this.user = user;
		this.password = password;
	}

	// 取得PasswordAuthentication对象
	protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(user, password);
	}
}