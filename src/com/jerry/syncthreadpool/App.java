package com.jerry.syncthreadpool;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * java线程池同步处理
 * @author Jerry Wang
 *
 */
public class App {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		List<String> lists = new ArrayList<String>();
		for (int i = 0; i < 100000; i++) {
			lists.add("jerry"+i);
		}
		
		List<SessionCallable<String>> callables= SyncThreadPool.getSessionCallable(lists);
		List<Future<String>> syncFnData = SyncThreadPool.syncFn(callables);
		List<String> results = new ArrayList<String>();
		for (Future<String> future : syncFnData) {
			if(future.isDone()){
				results.add(future.get()) ;
			}
		}
	}
}
