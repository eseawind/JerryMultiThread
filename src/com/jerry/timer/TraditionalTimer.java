package com.jerry.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建定时器传统方式
 * @author JerryWang
 *
 */
public class TraditionalTimer {
	
	public static void main(String[] args) {
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("bombing");
			}
		}, 10000, 3000);
		
		while(true) {
			System.out.println(new Date().getSeconds());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
