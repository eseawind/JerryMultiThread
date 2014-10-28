package com.jerry.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore同步工具
 * @author Jerry Wang
 *
 */
public class SemaphoreTest {
	public static void main(String[] args) {
		ExecutorService service =  Executors.newCachedThreadPool();
		final Semaphore semaphore = new Semaphore(3);
		for(int i = 0; i < 10; i++) {
			Runnable runable = new Runnable() {

				@Override
				public void run() {
					try {
						semaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					System.out.println("线程" + Thread.currentThread().getName() + "进入，"
						+	"当前已有" + (3 - semaphore.availablePermits() + "个并发"));
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("线程" + Thread.currentThread().getName() + "即将离开");
					
					semaphore.release();
					
					System.out.println("线程" + Thread.currentThread().getName() + "进入，"
							+	"当前已有" + (3 - semaphore.availablePermits() + "个并发"));
					
				}
			};
			service.execute(runable);
		}
	}
}
