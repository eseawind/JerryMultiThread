/**
 * 文件名称 : SendMail.java 
 * 项 目 名 : JavaMailWeb
 * 包    名 : com.jerry.mail.model  
 * 版权所有 : 版权所有(C)2012-2013
 * 创建作者 : Jerry Wang
 * 创建时间 : May 17, 2013 9:55:52 AM
 * 电子邮件 : jerry002@126.com
 * 当前版本 : v1.0 
 */
package com.jerry.mail;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
/**
 * SendMail.java
 * 发送邮件类
 * @author Jerry Wang
 * May 17, 2013 9:55:52 AM
 */
public class SendMail {
	private static String SMTPHost = ""; // SMTP服务器
	private static String username = ""; // 登录SMTP服务器的帐号
	private static String password = ""; // 登录SMTP服务器的密码
	private static String from = ""; // 发件人邮箱
	
	private Address[] to = null; // 收件人邮箱
	private String subject = ""; // 邮件标题
	private String content = ""; // 邮件内容
	private Address[] copyto = null;// 抄送邮件到
	private Session mailSession = null;
	private Transport transport = null;
	private ArrayList<String> filename = new ArrayList<String>(); // 附件文件名
	private static SendMail sendMail = null;
	private final static String charset = "UTF-8";
	
	/** 
	 * @Name : SendMail
	 * @Description : 无参数构造方法
	 * @param :     
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 9:59:47 AM 
	 */
	private SendMail() {}
	
	/**
	 * @Name : getMailInstantiate 
	 * @Description : 返回SendMail的对象
	 * @param : @return    
	 * @return : SendMail    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:02:46 AM
	 */
	public static SendMail getInstance() {
		if(null == sendMail) {
			synchronized (SendMail.class) {
				if(null == sendMail) {
					init();
					sendMail = new SendMail();
				}
			}
		}
		return sendMail;
	}
	
	private synchronized static void init() {
		SMTPHost = "smtp.126.com";
		username = "jerry002@126.com";
		from = username;
		password = getPassword();
	}

	/**
	 * @Name : connect 
	 * @Description : 连接SMTP邮件服务器
	 * @param :     
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:16:13 AM
	 */
	public void connect() {
		try {
			if(transport == null || !transport.isConnected()) {
				synchronized (this) {
					if(transport == null || !transport.isConnected()) {
						// 创建一个属性对象
						Properties props = new Properties();
						// 指定SMTP服务器
						props.put("mail.smtp.host", SMTPHost);
						// 指定是否需要SMTP验证
						props.put("mail.smtp.auth", "true");
						// 创建一个授权验证对象
						SmtpPop3Auth auth = new SmtpPop3Auth();
						auth.setAccount(username, password);
						// 创建一个Session对象
						mailSession = Session.getDefaultInstance(props, auth);
						// 设置是否调试
						mailSession.setDebug(false);
					//	if (transport != null)
							// 关闭连接
						//	transport.close();
						// 创建一个Transport对象
						transport = mailSession.getTransport("smtp");
						// 连接SMTP服务器
						transport.connect(SMTPHost, username, password);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Name : close 
	 * @Description : 关闭连接SMTP邮件服务器
	 * @param : @return    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:18:19 AM
	 */
	public void close() {
		try {
			if(transport.isConnected()) {
				synchronized (SendMail.class) {
					if(transport.isConnected()) {
						transport.close();
						transport = null;
					}
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Name : send 
	 * @Description : 发送邮件
	 * @param : @return    
	 * @return : String    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:18:19 AM
	 */
	public synchronized boolean send() {
		boolean result = false;
		try {
			// 连接smtp服务器
			sendMail.connect();
			// 创建一个MimeMessage 对象
			MimeMessage message = new MimeMessage(mailSession);

			// 指定发件人邮箱
			message.setFrom(new InternetAddress(from));
			// 指定收件人邮箱
			message.addRecipients(Message.RecipientType.TO, to);
			if (!"".equals(copyto))
				// 指定抄送人邮箱
				message.addRecipients(Message.RecipientType.CC, copyto);
			// 指定邮件主题
			message.setSubject(subject);
			// 指定邮件发送日期
			message.setSentDate(new Date());
			// 指定邮件优先级 1：紧急 3：普通 5：缓慢
			message.setHeader("X-Priority", "3");
			message.saveChanges();
			// 判断附件是否为空
			if (!filename.isEmpty()) {
				// 新建一个MimeMultipart对象用来存放多个BodyPart对象
				Multipart container = new MimeMultipart();
				// 新建一个存放信件内容的BodyPart对象
				BodyPart textBodyPart = new MimeBodyPart();
				// 给BodyPart对象设置内容和格式/编码方式
				textBodyPart.setContent(content, "text/html;charset="+charset);
				// 将含有信件内容的BodyPart加入到MimeMultipart对象中
				container.addBodyPart(textBodyPart);
				Iterator<String> fileIterator = filename.iterator();
				while (fileIterator.hasNext()) {// 迭代所有附件
					String attachmentString = fileIterator.next();
					// 新建一个存放信件附件的BodyPart对象
					BodyPart fileBodyPart = new MimeBodyPart();
					// 将本地文件作为附件
					FileDataSource fds = new FileDataSource(attachmentString);
					fileBodyPart.setDataHandler(new DataHandler(fds));
					// 处理邮件中附件文件名的中文问题
					String attachName = fds.getName();
					attachName = MimeUtility.encodeText(attachName);
					// 设定附件文件名
					fileBodyPart.setFileName(attachName);
					// 将附件的BodyPart对象加入到container中
					container.addBodyPart(fileBodyPart);
				}
				// 将container作为消息对象的内容
				message.setContent(container);
			} else {// 没有附件的情况
				message.setContent(content, "text/html;charset="+charset);
			}
			// 发送邮件
			Transport.send(message, message.getAllRecipients());
			if (transport != null)
				transport.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			sendMail.close();
		}
		
		return result;
	}
	
	/**
	 * @Name : setContent 
	 * @Description : 设置邮件内容
	 * @param : @param content    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:07:30 AM
	 */
	public void setContent(String content) {
		this.content = content;
	}
	
	/**
	 * @Name : setFilename 
	 * @Description : 设置附件名称
	 * @param : @param filename    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:08:27 AM
	 */
	public void setFilename(ArrayList<String> filename) {
		try {
			Iterator<String> iterator = filename.iterator();
			ArrayList<String> attachArrayList = new ArrayList<String>();
			while (iterator.hasNext()) {
				String attachment = iterator.next();
				// 解决文件名的中文问题
				attachment = MimeUtility.decodeText(attachment);
				// 将文件路径中的'\'替换成'/'
				attachment = attachment.replaceAll("\\\\", "/");
				attachArrayList.add(attachment);
			}
			this.filename = attachArrayList;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @Name : setSubject 
	 * @Description : 设置标题
	 * @param : @param subject    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:08:44 AM
	 */
	public void setSubject(String subject) {
		try {
			// 解决标题的中文问题
			subject = MimeUtility.encodeText(subject);
			this.subject = subject;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * @Name : setTo 
	 * @Description : 设置收件人邮箱
	 * @param : @param toto    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:09:09 AM
	 */
	public void setTo(String toto) {
		try {
			int i = 0;
			StringTokenizer tokenizer = new StringTokenizer(toto, ";");
			to = new Address[tokenizer.countTokens()];// 动态的决定数组的长度
			while (tokenizer.hasMoreTokens()) {
				String d = tokenizer.nextToken();
				
					d = MimeUtility.encodeText(d);
					to[i] = new InternetAddress(d);// 将字符串转换为整型
				
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Name : setCopy_to 
	 * @Description : 设置抄送
	 * @param : @param copyTo    
	 * @return : void    
	 * @author : Jerry Wang
	 * @DateTime : May 17, 2013 10:09:38 AM
	 */
	public void setCopyto(String copyTo) {
		try {
			int i = 0;
			StringTokenizer tokenizer = new StringTokenizer(copyTo, ";");
			copyto = new Address[tokenizer.countTokens()];// 动态的决定数组的长度
			while (tokenizer.hasMoreTokens()) {
				String tolen = tokenizer.nextToken();
				tolen = MimeUtility.encodeText(tolen);
				copyto[i] = new InternetAddress(tolen);// 将字符串转换为整型
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getPassword() {
		Encrypt encrypt = new Encrypt();
		encrypt.setKey(username);
		encrypt.setDesString("V2fwg3wuUDEI4xUTpYbzSA=="); 
		return encrypt.getStrM();
	}
	
}
