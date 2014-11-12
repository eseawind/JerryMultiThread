package com.jerry.wait;

/**
 * 线程等待的几种方法
 */
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadWait {
	final static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws Exception {
		join();

	}
	
	private void doSomeWork() {
		try {
			Thread.sleep((long) (Math.random() * 10000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void doSuperWork() {
		System.out.println("Super Worker begin at " + sdf.format(new Date()));
		try {
			Thread.sleep((long) (Math.random() * 10000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Super Worker end at " + sdf.format(new Date()));
	}
	
	static class JoinWorker extends Thread {
		String workerName;

		public JoinWorker(String workerName) {
			this.workerName = workerName;
		}

		public void run() {
			System.out.println("Sub Worker " + workerName
					+ " do work begin at " + sdf.format(new Date()));
			new ThreadWait().doSomeWork();// 做实际工作
			System.out.println("Sub Worker " + workerName
					+ " do work complete at " + sdf.format(new Date()));
		}
	}
	
	/**
	 * 使用线程自带的join方法，将子线程加到主线程中 主线程需要等待子线程完成才继续执行
	 * 
	 * @throws InterruptedException
	 */
	public static void join() throws InterruptedException {
		System.out.println("=========Test with join=====");
		JoinWorker worker1 = new JoinWorker("worker1");
		JoinWorker worker2 = new JoinWorker("worker2");
		worker1.start();
		worker2.start();
		worker1.join();
		worker2.join();
		doSuperWork();
	}

	/**
	 * 使用CountDownLatch，每个线程调用其countDown方法使计数器-1，
	 * 主线程调用await方法阻塞等待，直到CountDownLatch计数器为0时继续执行
	 * 
	 * @throws InterruptedException
	 */
	public static void countDownLatch() throws InterruptedException {
		System.out.println("=========Test with CountDownLatch=====");
		CountDownLatch latch = new CountDownLatch(2);
		CountDownLatchWorker worker1 = new CountDownLatchWorker("worker1",latch);
		CountDownLatchWorker worker2 = new CountDownLatchWorker("worker2",latch);
		worker1.start();
		worker2.start();
		// 主线程阻塞等待
		latch.await();
		doSuperWork();
	}

	/**
	 * CyclicBarrier类似于CountDownLatch也是个计数器， 不同的是CyclicBarrier的await()
	 * 方法没被调用一次，计数便会减少1，并阻塞住当前线程。当计数减至0时，阻塞解除，所有在此 CyclicBarrier 上面阻塞的线程开始运行。
	 * 在这之后，如果再次调用 await() 方法，计数就又会变成 N-1，新一轮重新开始
	 * CyclicBarrier初始时还可带一个Runnable的参数，
	 * 此Runnable任务在CyclicBarrier的数目达到后，所有其它线程被唤醒前被执行。
	 */
	public static void cyclicBarrier() throws InterruptedException,
			BrokenBarrierException {
		System.out.println("=========Test with CyclicBarrier=====");
		CyclicBarrier cb = new CyclicBarrier(2, new Runnable() {
			// 将主线程业务放到CyclicBarrier构造方法中，所有线程都到达Barrier时执行
			@SuppressWarnings("static-access")
			public void run() {
				new ThreadWait().doSuperWork();
			}
		});// 设定需要等待两个线程
		ExecutorService executor = Executors.newFixedThreadPool(2);
		CyclicBarrierWorker worker1 = new CyclicBarrierWorker("worker1", cb);
		CyclicBarrierWorker worker2 = new CyclicBarrierWorker("worker2", cb);
		executor.execute(worker1);
		executor.execute(worker2);
		executor.shutdown();
	}

	/**
	 * 使用ExecutorService的invokeAll方法调研callable集合，批量执行多个线程
	 * 在invokeAll方法结束之后，再执行主线程其他业务逻辑
	 * 
	 * @throws InterruptedException
	 */
	public static void callable() throws InterruptedException {
		System.out.println("=========Test with Callable=====");
		List<Callable<Integer>> callList = new ArrayList<Callable<Integer>>();
		ExecutorService exec = Executors.newFixedThreadPool(2);
		// 采用匿名内部类实现
		callList.add(new Callable<Integer>() {
			public Integer call() throws Exception {
				System.out.println("Sub Worker worker1 do work begin at "
						+ sdf.format(new Date()));
				new ThreadWait().doSomeWork();// 做实际工作
				System.out.println("Sub Worker worker1 do work complete at "
						+ sdf.format(new Date()));
				return 0;
			}
		});
		callList.add(new Callable<Integer>() {
			public Integer call() throws Exception {
				System.out.println("Sub Worker worker2 do work begin at "
						+ sdf.format(new Date()));
				new ThreadWait().doSomeWork();// 做实际工作
				System.out.println("Sub Worker worker2 do work complete at "
						+ sdf.format(new Date()));
				return 0;
			}
		});
		exec.invokeAll(callList);
		exec.shutdown();
		doSuperWork();

	}

	static class CountDownLatchWorker extends Thread {
		String workerName;

		CountDownLatch latch;

		public CountDownLatchWorker(String workerName, CountDownLatch latch) {
			this.workerName = workerName;
			this.latch = latch;
		}

		public void run() {
			System.out.println("Sub Worker " + workerName
					+ " do work begin at " + sdf.format(new Date()));
			new ThreadWait().doSomeWork();// 做实际工作
			System.out.println("Sub Worker " + workerName
					+ " do work complete at " + sdf.format(new Date()));
			latch.countDown();// 完成之后，计数器减一

		}
	}

	static class CyclicBarrierWorker extends Thread {
		String workerName;

		CyclicBarrier cb;

		public CyclicBarrierWorker(String workerName, CyclicBarrier cb) {
			super();
			this.workerName = workerName;
			this.cb = cb;
		}

		public void run() {
			System.out.println("Sub Worker " + workerName
					+ " do work begin at " + sdf.format(new Date()));
			new ThreadWait().doSomeWork();// 做实际工作
			System.out.println("Sub Worker " + workerName
					+ " do work complete at " + sdf.format(new Date()));
			try {
				// 等待其他未完成线程
				cb.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}