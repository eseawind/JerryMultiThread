package com.jerry.mail;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class SendMailServiceImpl implements SendMailService {

	@Override
	public void sendMail(final String to, final String subject, final String content) {
		ThreadPoolExecutor taskExecutor = new ScheduledThreadPoolExecutor(10);
		taskExecutor.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (SendMailServiceImpl.class) {
					SendMail sendMail = SendMail.getInstance();
					sendMail.setSubject(subject);
					sendMail.setTo(to);
					sendMail.setContent(content);
					long start = System.currentTimeMillis();
					sendMail.send();
					long end = System.currentTimeMillis();
					System.out.println("用时 ：" + (end - start) + "ms");
					System.out.println(Thread.currentThread() + "发送完成");
				}
			}
		});
	}

	
}
