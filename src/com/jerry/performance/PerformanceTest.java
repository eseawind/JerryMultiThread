package com.jerry.performance; 

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock、synchronized、Atomic性能测试
 * @author Wangjiajun 
 * @Email  wangjiajun@58.com
 *
 */
public class PerformanceTest {
	public static void test(int round, int threadNum) {
		new SynchronizedTest("synchronized", round, threadNum).testTime();
		new LockTest("lock", round, threadNum).testTime();
		new AtomicTest("atomic", round, threadNum).testTime();
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < 5; i++) {
			int round = 100000 * (i + 1);
			int threadNum = 5 * (i + 1);
			System.out.println("-------------------------");
			System.out.println("round:" + round + "; thread:" + threadNum);
			PerformanceTest.test(round, threadNum);
			
		}
	}
}

class SynchronizedTest extends Template{

	public SynchronizedTest(String id, int round, int threadNum) {
		super(id, round, threadNum);
	}

	@Override
	synchronized void sumValue() {
		super.countValue += super.preInit[index++%round];
	}

	@Override
	synchronized long getValue() {
		return super.countValue;
	}
	
}

class LockTest extends Template {
	ReentrantLock lock = new ReentrantLock();
	
	public LockTest(String id, int round, int threadNum) {
		super(id, round, threadNum);
	}

	@Override
	void sumValue() {
		try {
			lock.lock();
			super.countValue += super.preInit[index++%round];
		} finally {
			lock.unlock();
		}
		
	}

	@Override
	long getValue() {
		try {
			lock.lock();
			return super.countValue;
		} finally {
			lock.unlock();
		}
		
	}
	
}

class AtomicTest extends Template{

	public AtomicTest(String id, int round, int threadNum) {
		super(id, round, threadNum);
	}

	@Override
	void sumValue() {
		super.countValueAtmoic.addAndGet(super.preInit[indexAtomic.get()%round]);
	}

	@Override
	long getValue() {
		return super.countValueAtmoic.get();
	}
	
}

abstract class Template {
	public  String id;
	public int round;
	public int threadNum;
	public long countValue;
	public AtomicLong countValueAtmoic = new AtomicLong(0);
	public int[] preInit;
	public int index;
	public AtomicInteger indexAtomic = new AtomicInteger(0);
	Random r = new Random(47);
	// 任务栅栏，同批任务，先到达wait的任务挂起，一直等到全部任务到达制定的wait地点后，才能全部唤醒，继续执行
	private CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum * 2 + 1);

	public Template(String id, int round, int threadNum) {
		this.id = id;
		this.round = round;
		this.threadNum = threadNum;
		preInit = new int[round];
		for (int i = 0; i < preInit.length; i++) {
			preInit[i] = r.nextInt(100);
		}
	}

	abstract void sumValue();

	/*
	 * 对long的操作是非原子的，原子操作只针对32位 long是64位， 底层操作的时候分2个32位读写，因此不是线程安全
	 */
	abstract long getValue();

	public void testTime() {
		ExecutorService se = Executors.newCachedThreadPool();
		long start = System.nanoTime();
		// 同时开启2*ThreadNum个数的读写线程
		for (int i = 0; i < threadNum; i++) {
			se.execute(new Runnable() {
				public void run() {
					for (int i = 0; i < round; i++) {
						sumValue();
					}

					// 每个线程执行完同步方法后就等待
					try {
						cyclicBarrier.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}

				}
			});
			se.execute(new Runnable() {
				public void run() {

					getValue();
					try {
						// 每个线程执行完同步方法后就等待
						cyclicBarrier.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}

				}
			});
		}

		try {
			// 当前统计线程也wait,所以CyclicBarrier的初始值是threadNum*2+1
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		// 所有线程执行完成之后，才会跑到这一步
		long duration = System.nanoTime() - start;
		System.out.println(id + " = " + duration + "ms");

	}

}
