package com.jerry.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 传统方式实现线程互斥(增加Lock)
 * @author Jerry Wang
 *
 */
public class TraditionalThreadSynchronized {
	
	public static void main(String[] args) {
		new TraditionalThreadSynchronized().init();
	}
	
	private void init() {
		final Outputer outputer = new Outputer();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					outputer.output("xxxxxxxxxxxxxxxxxx");
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					outputer.output("yyyyyyyyyyyyyyyyyy");
				}
			}
		}).start();
	}
	
 	class Outputer{
//		public void output(String name) {
//			int len = name.length();
//			synchronized(this){
//				for(int i = 0; i < len; i++) {
//					System.out.print(name.charAt(i));
//				}
//				System.out.println();
//			}
//		}
		
// 		public void output(String name) {
//			int len = name.length();
//			synchronized(Outputer.class) {
//				for(int i = 0; i < len; i++) {
//					System.out.print(name.charAt(i));
//				}
//				System.out.println();
//			}
//		}
 		
//		public synchronized void output(String name) {
//			int len = name.length();
//			for(int i = 0; i < len; i++) {
//				System.out.print(name.charAt(i));
//			}
//			System.out.println();
//		}
		
		Lock lock = new ReentrantLock();
		public void output(String name) {
			int len = name.length();
			lock.lock();
			try{
				for(int i = 0; i < len; i++) {
					System.out.print(name.charAt(i));
				}
				System.out.println();
			} finally {
				lock.unlock();
			}
		}
	}
}
