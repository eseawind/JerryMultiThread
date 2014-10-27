package com.jerry.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 模拟一个缓存器
 * 
 * @author Jerry Wang
 * 
 */
public class CacheDemo {
	private Map<String, Object> cache = new HashMap<String, Object>();

	/**
	 * 使用 synchronized 进行互斥
	 * 
	 * @param key
	 * @return
	 */
	// public synchronized Object getData(String key) {
	// Object value = cache.get(key);
	// if(value == null) {
	// value = "xxxxxx"; // 实际是从数据库中取
	// }
	// return value;
	// }

	/**
	 * 使用读写锁
	 * 
	 * @param key
	 * @return
	 */
	private ReadWriteLock lock = new ReentrantReadWriteLock();

	public Object get(String id) {
		Object value = null;
		lock.readLock().lock();// 首先开启读锁，从缓存中去取
		try {
			value = cache.get(id);
			if (value == null) { // 如果缓存中没有释放读锁，上写锁
				lock.readLock().unlock();
				lock.writeLock().lock();
				try {
					if (value == null) {
						value = "aaa"; // 此时可以去数据库中查找，这里简单的模拟一下
					}
				} finally {
					lock.writeLock().unlock(); // 释放写锁
				}
				lock.readLock().lock(); // 然后再上读锁
			}
		} finally {
			lock.readLock().unlock(); // 最后释放读锁
		}
		return value;
	}

}
