package com.jerry.syncthreadpool;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SyncThreadPool {
	
	
	public static List<SessionCallable<String>> getSessionCallable(List<String> lists){
		List<SessionCallable<String>> callables = new LinkedList<SessionCallable<String>>();
		if(null != lists && lists.size() > 0){
			for (final String str : lists) {
				callables.add(new SessionCallable<String>() {
					@Override
					public String call() throws Exception {
						String temp = str+"123";
						System.err.println(Thread.currentThread()+":"+str+":"+temp);
						return temp;
					}
				});
			}
		}
		return callables;
	}
	
	
	public static <T> List<Future<T>> syncFn(List<SessionCallable<T>> callables){
		try {
			ExecutorService threadPool = Executors.newFixedThreadPool(5);
			if(null!=callables&&callables.size()>0){
				List<Future<T>> Futures = threadPool.invokeAll(callables);
				threadPool.shutdown();
				return Futures;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
}