package com.jerry.thread;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Callable与Future的应用
 * @author Jerry Wang
 *
 */
public class CallableAndFuture {
	public static void singleCallback() {
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
		Future<String> future = 
			threadPool.submit(new Callable<String> () {

			@Override
			public String call() throws Exception {
				Thread.sleep(2000);
				return "hello";
			}
			
		});
		
		System.out.println("等待结果");
		try {
			System.out.println("拿到结果" + future.get(3,TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public static void multiCallback() {
		
		ExecutorService threadPool2 = Executors.newFixedThreadPool(10);
		ExecutorCompletionService<Integer> executorCompletionService = new ExecutorCompletionService<Integer>(threadPool2);
		for(int i = 0; i < 10; i++) {
			final int seq = i;
			executorCompletionService.submit(new Callable<Integer>() {

				@Override
				public Integer call() throws Exception {
					Thread.sleep(new Random().nextInt(5000));
					return seq;
				}
				
			});
		}
		
		for(int i = 0; i < 10; i++) {
			try {
				System.out.println(executorCompletionService.take().get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		
	}
	public static void main(String[] args) {
//		CallableAndFuture.singleCallback();
		
		CallableAndFuture.multiCallback();
	}
}
